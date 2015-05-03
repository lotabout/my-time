(ns my-time.routes.auth
  (:require [my-time.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [my-time.db.core :as db]
            [my-time.session :as session]
            ))

(v/defvalidator valid-password
  {:default-message-format "Username or password invalid"}
  [password username]
  (if-let [pass (:password (first (db/get-user {:username username})))]
    (= password pass)))

(defn login-page [username & [errors]]
  (layout/render
    "login.html" {:errors errors :username username}))

;; (defn login! [{:keys [params session]}]
;;   (let [[errors bindings]
;;         (b/validate params
;;                     :username v/required
;;                     :password [v/required [valid-password (:username params)]])]
;;     (if errors
;;       (login-page (:username bindings) errors)
;;       (-> (redirect "/")
;;           (assoc :session (assoc session :identity (:username bindings)))))))
(defn login! [{:keys [params]}]
  (let [[errors bindings]
        (b/validate params
                    :username v/required
                    :password [v/required [valid-password (:username params)]])]
    (if errors
      (login-page (:username bindings) errors)
      (do (session/put! :identity (get bindings :username))
          (redirect "/")))))

;; (defn clear-session! [response]
;;   (-> response
;;       (assoc :session {})))
(defn logout []
  (do (session/clear!)
      (redirect "/login")))

(defroutes auth-routes
  (GET "/login" [] (login-page ""))
  (POST "/login" req (session/with-session req (login! req)))

  (GET "/logout" req (session/with-session req (logout))))
