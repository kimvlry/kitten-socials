CREATE TABLE if not exists public.refresh_tokens
(
    id               BIGSERIAL PRIMARY KEY,
    token            TEXT        NOT NULL UNIQUE,
    user_id          BIGINT      NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
    expiry_timestamp TIMESTAMPTZ NOT NULL,
    revoked          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ          DEFAULT now()
);
