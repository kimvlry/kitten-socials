CREATE TABLE IF NOT EXISTS owners
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    birth_timestamp TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kittens
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    birth_timestamp TIMESTAMP,
    breed           VARCHAR(50),
    coat_color      VARCHAR(50),
    owner_id        BIGINT NOT NULL REFERENCES owners (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship (
    kitten1_id BIGINT NOT NULL REFERENCES kittens (id) ON DELETE CASCADE,
    kitten2_id BIGINT NOT NULL REFERENCES kittens (id) ON DELETE CASCADE,
    PRIMARY KEY (kitten1_id, kitten2_id)
);