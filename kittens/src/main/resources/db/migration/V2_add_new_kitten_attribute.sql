ALTER TABLE kittens
    ADD COLUMN purr_loudness_rate INTEGER CHECK (
        purr_loudness_rate IS NULL OR (purr_loudness_rate BETWEEN 0 AND 10)
        );
