
CREATE TABLE residue
(
    scop_id VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL,
    order_number INTEGER NOT NULL,
    residue_number INTEGER NOT NULL,
    insert_code VARCHAR NULL,
    residue_code VARCHAR NOT NULL, 
    gram VARCHAR NULL, 
    max_tf DOUBLE PRECISION NOT NULL,
    ssa VARCHAR NULL,
    phi DOUBLE PRECISION NULL,
    psi DOUBLE PRECISION NULL,
    phi_x DOUBLE PRECISION NULL,
    phi_y DOUBLE PRECISION NULL,
    psi_x DOUBLE PRECISION NULL,
    psi_y DOUBLE PRECISION NULL
);

CREATE UNIQUE INDEX idx_residue_unique ON residue (scop_id, order_number);
