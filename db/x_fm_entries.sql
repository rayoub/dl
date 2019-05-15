
TRUNCATE fm_entries;

COPY fm_entries (casp, pdb_id) FROM '/home/ayoub/git/dl/data/fm_entries.txt' WITH (DELIMITER ',');

