
CREATE TABLE gram
(
    scop_id VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL,
    order_number INTEGER NOT NULL,
    residue_number INTEGER NOT NULL,
    insert_code VARCHAR NULL,
    phi DOUBLE PRECISION NOT NULL,
    psi DOUBLE PRECISION NOT NULL,
    descriptor VARCHAR NOT NULL,
    max_tf DOUBLE PRECISION NOT NULL,

    residue_code_1 VARCHAR NOT NULL, 
    max_tf_1 DOUBLE PRECISION NOT NULL,
    ss8_1 VARCHAR NULL,
    ss3_1 VARCHAR NULL,
    
    residue_code_2 VARCHAR NOT NULL, 
    max_tf_2 DOUBLE PRECISION NOT NULL,
    ss8_2 VARCHAR NULL,
    ss3_2 VARCHAR NULL,
    
    residue_code_3 VARCHAR NOT NULL, 
    max_tf_3 DOUBLE PRECISION NOT NULL,
    ss8_3 VARCHAR NULL,
    ss3_3 VARCHAR NULL
);

CREATE UNIQUE INDEX idx_gram_unique ON gram (scop_id, order_number);

