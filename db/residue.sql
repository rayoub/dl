
CREATE TABLE residue
(
    scop_id VARCHAR NOT NULL,
    atom_number INTEGER NOT NULL,
    residue_number INTEGER NOT NULL,
    insert_code VARCHAR NULL,
    residue_code VARCHAR NOT NULL, 
    ssa VARCHAR NOT NULL,
    sse VARCHAR NOT NULL,
    phi NUMERIC NULL,
    psi NUMERIC NULL,
    descriptor VARCHAR NULL,
    ca_x NUMERIC NOT NULL,
    ca_y NUMERIC NOT NULL,
    ca_z NUMERIC NOT NULL,
    cb_x NUMERIC NULL,
    cb_y NUMERIC NULL,
    cb_z NUMERIC NULL
);

CREATE UNIQUE INDEX idx_residue_unique ON residue (scop_id, atom_number);
