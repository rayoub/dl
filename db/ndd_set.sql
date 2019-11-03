
CREATE TABLE ndd_set
(
    pivot_db_id VARCHAR NOT NULL,
    member_db_id VARCHAR NOT NULL,
    similarity NUMERIC NOT NULL
);

CREATE UNIQUE INDEX idx_ndd_set_unique ON ndd_set (pivot_db_id, member_db_id);
