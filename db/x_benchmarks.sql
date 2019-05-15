
TRUNCATE benchmark;

COPY benchmark (name, model, num, res, missing, ss) FROM '/home/ayoub/git/dl/benchmarks/CB513.txt' WITH (DELIMITER ',');
COPY benchmark (name, model, num, res, missing, ss) FROM '/home/ayoub/git/dl/benchmarks/CASP11.txt' WITH (DELIMITER ',');

UPDATE benchmark SET ss = 'C' WHERE ss = 'L';

