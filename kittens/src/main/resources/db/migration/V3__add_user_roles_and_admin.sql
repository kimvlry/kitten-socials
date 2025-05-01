CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE user_owner_mapping
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES owners (id) ON DELETE CASCADE
);

INSERT INTO roles (name)
VALUES ('ROLE_ADMIN');
INSERT INTO roles (name)
VALUES ('ROLE_USER');

INSERT INTO owners (name)
VALUES ('Admin');

INSERT INTO users (username, password, email)
VALUES ('admin',
        '$2a$10$4b8t9z3Yk6y3v7u8w9x0se4Qz6y7v8u9w0x1s2t3u4v5w6x7y8z9', -- bcrypt "admin"
        'admin@example.com');

INSERT INTO user_owner_mapping (user_id, owner_id)
SELECT u.id, o.id
FROM users u,
     owners o
WHERE u.username = 'admin'
  AND o.name = 'Admin';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u,
     roles r
WHERE u.username = 'admin'
  AND r.name = 'ROLE_ADMIN';