CREATE TABLE IF NOT EXISTS owners
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    birth_timestamp TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS kittens
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    birth_timestamp TIMESTAMP    NOT NULL,
    breed           VARCHAR(50)  NOT NULL,
    owner_id        BIGINT REFERENCES owners (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship (
    kitten1_id BIGINT NOT NULL REFERENCES kittens (id) ON DELETE CASCADE,
    kitten2_id BIGINT NOT NULL REFERENCES kittens (id) ON DELETE CASCADE,
    PRIMARY KEY (kitten1_id, kitten2_id)
);