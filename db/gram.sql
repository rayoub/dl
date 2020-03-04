
CREATE TABLE gram
(
    scop_id VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL,
    order_number INTEGER NOT NULL,
    residue_number INTEGER NOT NULL,
    insert_code VARCHAR NULL,
    residue_code_1 VARCHAR NOT NULL, 
    residue_code_2 VARCHAR NOT NULL, 
    residue_code_3 VARCHAR NOT NULL,
    max_tf DOUBLE PRECISION NOT NULL,
    phi DOUBLE PRECISION NOT NULL,
    psi DOUBLE PRECISION NOT NULL,
    descriptor VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_gram_unique ON gram (scop_id, order_number);

