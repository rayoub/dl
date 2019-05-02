
CREATE TABLE aa_type
(
    symbol VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_aa_type_unique ON aa_type (symbol);
