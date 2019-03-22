
CREATE TABLE map
(
    pdb_id VARCHAR NOT NULL,
    chain VARCHAR NOT NULL,
    residue_number_1 INTEGER NULL,
    insert_code_1 VARCHAR NULL,
    residue_number_2 INTEGER NULL,
    insert_code_2 VARCHAR NULL,
    text VARCHAR NOT NULL,
    len INTEGER NOT NULL
);

CREATE UNIQUE INDEX idx_map_unique ON map (pdb_id, chain);

