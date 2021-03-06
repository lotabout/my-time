(ns my-time.middleware
  (:require [my-time.session :as session]
            [my-time.layout :refer [*servlet-context*]]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.accessrules :refer [wrap-access-rules success]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated?]]
            ))

(defn wrap-servlet-context [handler]
  (fn [request]
    (binding [*servlet-context*
              (if-let [context (:servlet-context request)]
                ;; If we're not inside a serlvet environment
                ;; (for example when using mock requests), then
                ;; .getContextPath might not exist
                (try (.getContextPath context)
                     (catch IllegalArgumentException _ context)))]
      (handler request))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (timbre/error t)
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body "<body>
                  <h1>Something very bad has happened!</h1>
                  <p>We've dispatched a team of highly trained gnomes to take care of the problem.</p>
                </body>"}))))

;;; authentication
(def rules
  [{:uri "/login"
    :handler success}
   {:uri "/*"
    :handler authenticated?}])

(defn on-error [request value]
  (redirect "/login"))

(defn development-middleware [handler]
  (if (env :dev)
    (-> handler
        wrap-error-page
        wrap-exceptions)
    handler))


(defn production-middleware [handler]
  (-> handler
      
      (wrap-access-rules {:rules rules :on-error on-error})
      (wrap-authentication (session-backend))
      (wrap-restful-format :formats [:json-kw :edn :transit-json :transit-msgpack])
      (wrap-idle-session-timeout
        {:timeout (* 60 30)
         :timeout-response (redirect "/")})
      (wrap-defaults
        (assoc-in site-defaults [:session :store] (memory-store session/mem)))
      wrap-servlet-context
      wrap-internal-error))

