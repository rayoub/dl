
TRUNCATE benchmark;

COPY benchmark (name, model, num, res, missing, ss) FROM '/home/ayoub/git/dl/test/CB513.txt' WITH (DELIMITER ',');
COPY benchmark (name, model, num, res, missing, ss) FROM '/home/ayoub/git/dl/test/CASP11.txt' WITH (DELIMITER ',');

UPDATE benchmark SET ss = 'C' WHERE ss = 'L';
UPDATE benchmark SET ss = '_' WHERE missing = 1;

