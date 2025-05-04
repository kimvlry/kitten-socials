DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.columns
                       WHERE table_schema = 'public'
                         AND table_name = 'kittens'
                         AND column_name = 'purr_loudness_rate') THEN
            ALTER TABLE kittens
                ADD COLUMN purr_loudness_rate INTEGER CHECK (
                    purr_loudness_rate IS NULL OR (purr_loudness_rate BETWEEN 0 AND 10)
                    );
        END IF;
    END
$$;