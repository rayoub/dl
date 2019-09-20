
CREATE TABLE pp_sequence
(
    scop_id VARCHAR NOT NULL,
    text VARCHAR NOT NULL,
    len INTEGER NOT NULL,
    missing_len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_pp_sequence_unique ON pp_sequence (scop_id);
