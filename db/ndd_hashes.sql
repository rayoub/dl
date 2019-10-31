
CREATE TABLE ndd_hashes
(
    db_id VARCHAR NOT NULL,
    min_hashes INTEGER ARRAY NOT NULL,
    band_hashes INTEGER ARRAY NOT NULL
);

CREATE UNIQUE INDEX idx_ndd_hashes_unique ON ndd_hashes (db_id);

