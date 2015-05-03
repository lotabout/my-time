(ns my-time.session
  (:require [cronj.core :refer [cronj]]))

(defonce mem (atom {}))

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn- expired? [[id session]]
  (pos? (- (:ring.middleware.session-timeout/idle-timeout session) (current-time))))

(defn clear-expired-sessions []
  (clojure.core/swap! mem #(->> % (filter expired?) (into {}))))

(def cleanup-job
  (cronj
    :entries
    [{:id "session-cleanup"
      :handler (fn [_ _] (clear-expired-sessions))
      :schedule "* /30 * * * * *"
      :opts {}}]))

;;; utils for manipulating sessions.

(declare ^:dynamic *session*)

(defn get
  ([k] (get k nil))
  ([k default] (clojure.core/get @*session* k default)))

(defn put!
  "Associates the key with the given value in the session"
  [k v]
  (clojure.core/swap! *session* assoc k v))

(defn remove!
  "Remove a key from the session"
  [k]
  (clojure.core/swap! *session* dissoc k))

(defn get-in
  "Gets the value at the path specified by the vector ks from the session,
  returns nil if it doesn't exist."
  ([ks] (get-in ks nil))
  ([ks default]
    (clojure.core/get-in @*session* ks default)))

(defn clear!
  "Remove all data from the session and start over cleanly."
  []
  (reset! *session* {}))

(defmacro with-session
  "Bind session from request to variable session and save them to the response "
  [request & body]
  `(binding [*session* (atom (:session ~request))]
     (assoc (do ~@body) :session @*session*)))
