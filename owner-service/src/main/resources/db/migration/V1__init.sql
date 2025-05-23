CREATE TABLE IF NOT EXISTS owners
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    birth_date DATE
);