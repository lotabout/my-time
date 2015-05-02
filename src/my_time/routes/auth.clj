(ns my-time.routes.auth
  (:require [my-time.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            ))

(defn login-page []
  (layout/render
    "login.html" {}))

(defn login! [{:keys [params session]}]
  (-> (redirect "/")
      (assoc :session (assoc session :identity "foo"))))

(defn clear-session! []
  (-> (redirect "/login")
      (assoc :session {})))

(defroutes auth-routes
  (GET "/login" [] (login-page))
  (POST "/login" req (login! req))

  (GET "/logout" req (clear-session!)))
