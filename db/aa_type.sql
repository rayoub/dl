
CREATE TABLE aa_type
(
    name VARCHAR NOT NULL,
    three_code VARCHAR NOT NULL,
    one_code VARCHAR NOT NULL,
    polar INTEGER NOT NULL,
    not_polar INTEGER NOT NULL,
    acidic INTEGER NOT NULL,
    not_acidic INTEGER NOT NULL,
    basic INTEGER NOT NULL,
    not_basic INTEGER NOT NULL,
    aromatic INTEGER NOT NULL,
    not_aromatic INTEGER NOT NULL,
    aliphatic INTEGER NOT NULL,
    not_aliphatic INTEGER NOT NULL,
    sulfur INTEGER NOT NULL,
    not_sulfur INTEGER NOT NULL,
    hydroxyl INTEGER NOT NULL,
    not_hydroxyl INTEGER NOT NULL,
    flexible INTEGER NOT NULL,
    not_flexible INTEGER NOT NULL,
    rigid INTEGER NOT NULL,
    not_rigid INTEGER NOT NULL,
    hydrophobicity NUMERIC NOT NULL
);

CREATE UNIQUE INDEX idx_aa_type_unique ON aa_type (name);
