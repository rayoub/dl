
CREATE TABLE target
(
    target_id VARCHAR NOT NULL,
    order_number INTEGER NOT NULL,
    residue_number INTEGER NOT NULL,
    insert_code VARCHAR NULL,
    residue_code VARCHAR NOT NULL, 
    phi DOUBLE PRECISION NULL,
    psi DOUBLE PRECISION NULL,
    descriptor VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_target_unique ON target (target_id, order_number);
