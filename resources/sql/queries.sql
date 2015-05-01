-- name: create-user!
-- create a new user
insert into users
(username, password)
values (:username, :password)

-- name: update-user!
-- update the password of a user
update users
set password = :password
where username = :username

-- name: disable-user!
-- disable an user account
update users
set is_active = false
where username = :username

-- name: enable-user!
-- enable an user account
update users
set is_active = true
where username = :username

-- name: delete-user!
-- delete a user account
delete from users
where username = :username

-- name: get-user
-- retrieve a used given the id.
select * from users
where username = :username
