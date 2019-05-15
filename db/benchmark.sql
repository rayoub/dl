
CREATE TABLE benchmark
(
    name VARCHAR NOT NULL,
    model VARCHAR NOT NULL,
    num INTEGER NOT NULL,
    res VARCHAR NOT NULL,
    missing INTEGER NOT NULL,
    ss VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_benchmark_unique ON benchmark (name, model, num);
