
CREATE TABLE valid_gram AS
SELECT
    g.*
FROM
    gram g
    INNER JOIN entries e
        ON e.pdb_id = g.pdb_id
    INNER JOIN astral_95 a
        ON a.scop_id = g.scop_id
WHERE
    g.max_tf < 300.0
    AND e.resolution < 2.0;

