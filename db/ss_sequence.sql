
CREATE TABLE ss_sequence
(
    scop_id VARCHAR NOT NULL,
    text VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_ss_sequence_unique ON ss_sequence (scop_id);
