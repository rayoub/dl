
CREATE TABLE residue
(
    scop_id VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL,
    order_number INTEGER NOT NULL,
    residue_number INTEGER NOT NULL,
    insert_code VARCHAR NULL,
    residue_code VARCHAR NOT NULL, 
    ssa VARCHAR NULL,
    phi DOUBLE PRECISION NULL,
    psi DOUBLE PRECISION NULL,
    descriptor VARCHAR NULL,
    ca_x DOUBLE PRECISION NOT NULL,
    ca_y DOUBLE PRECISION NOT NULL,
    ca_z DOUBLE PRECISION NOT NULL,
    cb_x DOUBLE PRECISION NULL,
    cb_y DOUBLE PRECISION NULL,
    cb_z DOUBLE PRECISION NULL,
    ck_x DOUBLE PRECISION NULL,
    ck_y DOUBLE PRECISION NULL,
    ck_z DOUBLE PRECISION NULL
);

CREATE UNIQUE INDEX idx_residue_unique ON residue (scop_id, order_number);
