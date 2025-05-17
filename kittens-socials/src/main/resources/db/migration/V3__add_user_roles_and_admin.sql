CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_owner_mapping
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    UNIQUE (user_id, owner_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES owners (id) ON DELETE CASCADE
);

INSERT INTO roles (name)
VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name)
VALUES ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO owners (name)
VALUES ('Admin')
ON CONFLICT DO NOTHING;

INSERT INTO users (username, password, email)
VALUES ('admin',
        '$2a$10$4b8t9z3Yk6y3v7u8w9x0se4Qz6y7v8u9w0x1s2t3u4v5w6x7y8z9', -- encoded "admin"
        'admin@example.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_owner_mapping (user_id, owner_id)
SELECT u.id, o.id
FROM users u
         JOIN owners o ON u.username = 'admin' AND o.name = 'Admin'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'admin')
  AND EXISTS (SELECT 1 FROM owners WHERE name = 'Admin')
ON CONFLICT (user_id, owner_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON u.username = 'admin' AND r.name = 'ROLE_ADMIN'
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'admin')
  AND EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN')
ON CONFLICT (user_id, role_id) DO NOTHING;