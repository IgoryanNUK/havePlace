create table if not exists admins (
    admin_id serial primary key,
    admin_name varchar(50) not null,
    admin_vk_id bigint unique,
    role varchar(50) check (role in ('OWNER', 'CLIENT', 'ADMIN', 'DEVELOPER'))
)