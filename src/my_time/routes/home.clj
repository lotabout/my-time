(ns my-time.routes.home
  (:require [my-time.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [my-time.session :as session]))

;; (declare ^:dynamic session)

;; (defn get
;;   ([k] (get k nil))
;;   ([k default] (clojure.core/get @session k default)))

;; (defmacro with-session
;;   "Bind session from request to variable session and save them to the response "
;;   [request & body]
;;   `(binding [session (atom (:session ~request))]
;;      ~@body))

;; (defn with-session [request fn]
;;   (binding [session (:session request)]
;;     ()))

(defn home-page []
  (layout/render
    "home.html" {:username (session/get :identity)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" req (session/with-session req (home-page)))
  (GET "/about" [] (about-page)))
