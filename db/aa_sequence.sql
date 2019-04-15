
CREATE TABLE aa_sequence
(
    scop_id VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL,
    cl VARCHAR NOT NULL,
    cf INTEGER NOT NULL,
    sf INTEGER NOT NULL,
    fa INTEGER NOT NULL,
    chain VARCHAR NOT NULL,
    residue_number_1 INTEGER NULL,
    insert_code_1 VARCHAR NULL,
    residue_number_2 INTEGER NULL,
    insert_code_2 VARCHAR NULL,
    text VARCHAR NOT NULL,
    len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_aa_sequence_unique ON aa_sequence (scop_id);
