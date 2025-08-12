create table if not exists prices (
    start_time time,
    end_time time not null,
    price int check(price > 0)
)