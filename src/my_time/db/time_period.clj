(ns my-time.db.time-period
  (:require
    [yesql.core :refer [defqueries]]
    [my-time.db.core :refer [db-spec]]))

(defqueries "sql/time-period.sql" {:connection db-spec})

(defn default-period
  [& {:keys [start_time end_time username task_id category_id comments]}]
  (let [current_time (java.util.Date.)]
    {:start_time (if start_time start_time (java.util.Date. (.getTime current_time)))
     :end_time (if end_time end_time (java.util.Date. (.getTime current_time)))
     :username username
     :task_id task_id
     :category_id category_id
     :comments comments}))

(defn override [period & new_val]
  (merge period (apply hash-map new_val)))
