CREATE TYPE kitten_breed AS ENUM (
    'BRITISH_SHORTHAIR',
    'PERSIAN',
    'MAINE_COON',
    'SIAMESE',
    'RAGDOLL',
    'ABYSSINIAN',
    'SIBERIAN',
    'BENGAL',
    'CORNISH_REX',
    'SPHYNX',
    'TOYGER',
    'MUNCHKIN',
    'SCOTTISH_FOLD'
    );

CREATE TYPE kitten_coat_color AS ENUM (
    'ESPRESSO',
    'LATTE',
    'CAPPUCCINO',
    'MOCHA',
    'MACCHIATO',
    'FRAPPE',
    'AMERICANO',
    'RISTRETTO',
    'CARAMEL',
    'CORTADO'
    );

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
    breed           kitten_breed NOT NULL,
    coat_color      kitten_coat_color NOT NULL,
    owner_id        BIGINT NOT NULL REFERENCES owners (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship (
    kitten1_id BIGINT NOT NULL REFERENCES kittens (id) ON DELETE CASCADE,
    kitten2_id BIGINT NOT NULL REFERENCES kittens (id) ON DELETE CASCADE,
    PRIMARY KEY (kitten1_id, kitten2_id)
);