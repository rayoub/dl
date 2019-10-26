
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
    phi_x DOUBLE PRECISION NULL,
    phi_y DOUBLE PRECISION NULL,
    psi_x DOUBLE PRECISION NULL,
    psi_y DOUBLE PRECISION NULL,
    phil_x DOUBLE PRECISION NULL,
    phir_x DOUBLE PRECISION NULL,
    spl_x DOUBLE PRECISION NULL,
    spl_y DOUBLE PRECISION NULL,
    spl_z DOUBLE PRECISION NULL,
    spr_x DOUBLE PRECISION NULL,
    spr_y DOUBLE PRECISION NULL,
    spr_z DOUBLE PRECISION NULL
);

CREATE UNIQUE INDEX idx_residue_unique ON residue (scop_id, order_number);
