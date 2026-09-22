create table task
(
    id          bigserial primary key,
    title       varchar(100) not null,
    description varchar(300),
    status      varchar(30),
    due_date    date         not null,
    category    varchar(30)
);