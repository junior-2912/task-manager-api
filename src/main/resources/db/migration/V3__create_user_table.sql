create table users(
    id bigserial primary key,
    name varchar(150) not null,
    email varchar(150) unique not null,
    password varchar(150) not null,
    role varchar(255) not null
);