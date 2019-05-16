
TRUNCATE entries;

COPY entries (pdb_id, deposit_date, resolution) FROM '/home/ayoub/git/dl/train/entries.txt' WITH (DELIMITER ',');

