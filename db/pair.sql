
CREATE TABLE pair
(
    scop_id VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL,
    max_tf DOUBLE PRECISION NOT NULL,
    residue_code_1 VARCHAR NOT NULL,
    residue_code_2 VARCHAR NOT NULL,
    descriptor_1 VARCHAR NOT NULL,
    descriptor_2 VARCHAR NOT NULL
);

