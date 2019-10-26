
CREATE TABLE cil_sequence
(
    scop_id VARCHAR NOT NULL,
    seq VARCHAR NOT NULL,
    weights VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_cil_sequence_unique ON cil_sequence (scop_id);
