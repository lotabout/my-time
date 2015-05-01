-- create user table serve as login information
create table if not exists users
(username varchar(50) primary key,
 password varchar(120) not null,
 is_active boolean default true);

-- create table to represent a category
create table if not exists categories
(category_id int primary key auto_increment,
 username varchar(50) not null,
 foreign key (username) references users(username) on delete cascade,
 name varchar(50) not null,
 color varchar(20) default '#FFF',
 description varchar(200));
 
-- create table to represent tags
create table if not exists tags
(tag_name varchar(50) primary key,
 username varchar(50) not null,
 foreign key (username) references users(username) on delete cascade,
 color varchar(20) default '#FFF');

-- create table to represent a task
create table if not exists tasks
(task_id int primary key,
 username varchar(50) not null,
 foreign key (username) references users(username) on delete cascade,
 title varchar(200) not null,
 details varchar(500),
 category_id int,
 foreign key (category_id) references categories(category_id) on delete set null,
 add_time timestamp default current_timestamp not null,
 schedule_time timestamp,
 deadline timestamp not null);
 
-- create table for task-tags
create table if not exists task_tags
(task_id int,
 foreign key(task_id) references tasks(task_id) on delete cascade,
 tag_name varchar(50),
 foreign key(tag_name) references tags(tag_name) on delete cascade,
 constraint pk_task_tags primary key(task_id, tag_name));

-- create table for task clocking
create table if not exists clocking
(task_id int,
 foreign key(task_id) references tasks(task_id) on delete cascade,
 start_time timestamp not null,
 end_time timestamp not null,
 constraint pk_clocking primary key (task_id, start_time));

-- create table to represent a time period.
create table if not exists time_periods
(start_time timestamp,
 end_time timestamp,
 username varchar(50) not null,
 foreign key(username) references users(username) on delete cascade,
 task_id int,
 foreign key(task_id) references tasks(task_id) on delete set null,
 category_id int,
 foreign key(category_id) references categories(category_id) on delete set null,
 comments varchar(200),
 constraint pk_time_periods primary key(start_time, end_time));
