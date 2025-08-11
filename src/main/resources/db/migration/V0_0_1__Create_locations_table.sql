create table if not exists locations (
    location_id serial PRIMARY KEY,
    location_name varchar(50) not null,
    address varchar(50),
    max_number_of_players int check(max_number_of_players > 0),
    photos varchar(100)
)