
CREATE TABLE fm_entries
(
    casp VARCHAR NOT NULL,
    pdb_id VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_fm_entries_unique ON fm_entries (casp, pdb_id);
