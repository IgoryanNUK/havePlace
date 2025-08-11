create table if not exists events (
    event_id serial primary key,
    event_time timestamp not null,
    booking_id int references bookings(booking_id),
    client_id int references clients(client_id),
    admin_id int references admins(admin_id),
    operation_type varchar(20) not null,
    comments varchar(50),
    admin_added_id int references admins(admin_id)
)