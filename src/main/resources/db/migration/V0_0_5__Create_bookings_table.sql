create table if not exists bookings (
    booking_id serial primary key,
    booking_date date not null,
    booking_start_time time not null,
    booking_end_time time not null,
    location_id int references locations(location_id),
    admin_id int references admins(admin_id),
    client_id int references clients(client_id),
    device varchar(100),
    number_of_players int check(number_of_players >= 0),
    comments varchar(200),
    status varchar(50) check(status in ('FREE', 'NEW', 'CONFIRMED', 'LOCKED')),
    is_available boolean default true,
    unique(booking_date, booking_start_time, location_id)
)