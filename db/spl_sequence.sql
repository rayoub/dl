
CREATE TABLE spl_sequence
(
    scop_id VARCHAR NOT NULL,
    seq VARCHAR NOT NULL,
    weights VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_spl_sequence_unique ON spl_sequence (scop_id);
