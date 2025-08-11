create table if not exists clients (
    client_id serial primary key,
    client_name varchar(50) not null,
    client_phone varchar(13) not null,
    client_vk_id bigint not null unique
)