
CREATE TABLE sequence_assignments
(
    scop_id VARCHAR NOT NULL,
    text VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_sequence_assignments_unique ON sequence_assignments (scop_id);
