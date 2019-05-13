
TRUNCATE entries;

COPY entries (pdb_id, deposit_date, resolution) FROM '/home/ayoub/git/dl/data/entries.txt' WITH (DELIMITER ',');

