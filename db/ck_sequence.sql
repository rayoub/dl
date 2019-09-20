
CREATE TABLE ck_sequence
(
    scop_id VARCHAR NOT NULL,
    text VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_ck_sequence_unique ON ck_sequence (scop_id);
