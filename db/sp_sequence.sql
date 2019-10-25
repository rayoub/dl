
CREATE TABLE sp_sequence
(
    scop_id VARCHAR NOT NULL,
    seq VARCHAR NOT NULL,
    weights VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_sp_sequence_unique ON sp_sequence (scop_id);
