UPDATE locations SET is_existing = true WHERE location_name in ('Таверна', 'Лес');

UPDATE locations SET is_existing = false WHERE location_name = 'Космос';