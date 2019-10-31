
CREATE TABLE ndd_grams
(
    db_id VARCHAR NOT NULL,
    grams INTEGER ARRAY NOT NULL
);

CREATE UNIQUE INDEX idx_ndd_grams_unique ON ndd_grams (db_id);

