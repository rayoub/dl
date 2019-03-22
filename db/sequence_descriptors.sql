
CREATE TABLE sequence_descriptors
(
    scop_id VARCHAR NOT NULL,
    text VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_sequence_descriptors_unique ON sequence_descriptors (scop_id);
