(ns my-time.routes.auth
  (:require [my-time.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [my-time.db.core :as db]
            ))

(v/defvalidator valid-password
  {:default-message-format "Username or password invalid"}
  [password username]
  (if-let [pass (:password (first (db/get-user {:username username})))]
    (= password pass)))

(defn login-page [username & [errors]]
  (layout/render
    "login.html" {:errors errors :username username}))

(defn login! [{:keys [params session]}]
  (let [[errors bindings]
        (b/validate params
                    :username v/required
                    :password [v/required [valid-password (:username params)]])]
    (if errors
      (login-page (:username bindings) errors)
      (-> (redirect "/")
          (assoc :session (assoc session :identity (:username bindings)))))))

(defn clear-session! [response]
  (-> response
      (assoc :session {})))

(defroutes auth-routes
  (GET "/login" [] (login-page ""))
  (POST "/login" req (login! req))

  (GET "/logout" req (clear-session! (redirect "/login"))))
