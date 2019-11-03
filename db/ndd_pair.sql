
CREATE TABLE ndd_pair
(
    db_id_1 VARCHAR NOT NULL,
    db_id_2 VARCHAR NOT NULL,
    similarity NUMERIC NOT NULL
);

CREATE UNIQUE INDEX idx_ndd_pair_unique ON ndd_pair (db_id_1, db_id_2);

