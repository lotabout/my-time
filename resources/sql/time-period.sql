-- name: get-last-period
select start_time from time_periods
where start_time = end_time

-- name: insert-period!
insert into time_periods
(start_time, end_time, username, task_id, category_id, comments)
values (:start_time, :end_time, :username, :task_id, :category_id, :comments)

-- name: update-period!
update time_periods
set end_time = :end-time, task_id = :task-id, category_id=:category-id,
comments=:comments
where start_time = :start_time and end_time = :end_time

-- name: delete-period!
delete from time_periods
where start_time = :start_time and end_time = :end_time
