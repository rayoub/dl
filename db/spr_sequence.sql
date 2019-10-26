
CREATE TABLE spr_sequence
(
    scop_id VARCHAR NOT NULL,
    seq VARCHAR NOT NULL,
    weights VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_spr_sequence_unique ON spr_sequence (scop_id);
