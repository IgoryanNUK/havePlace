create table if not exists regular_events (
    reg_event_id serial primary key,
    name varchar(50) not null,
    location_id int references locations(location_id) not null,
    day_of_week varchar(9) check(day_of_week in ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    start_time time not null,
    end_time time not null,
    client_id int references clients(client_id),
    number_of_players int check(number_of_players >= 0),
    start_date date,
    end_date date,
    unique(location_id, day_of_week, start_time, end_time, start_date, end_date)
)