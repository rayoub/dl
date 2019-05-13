
CREATE TABLE entries
(
    pdb_id VARCHAR NOT NULL,
    deposit_date DATE NOT NULL,
    resolution NUMERIC NOT NULL
);

CREATE UNIQUE INDEX idx_entries_unique ON entries (pdb_id);
