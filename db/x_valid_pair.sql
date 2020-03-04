
CREATE TABLE valid_pair AS
SELECT
    p.*
FROM
    pair p
    INNER JOIN entries e
        ON e.pdb_id = p.pdb_id
    INNER JOIN astral_95 a
        ON a.scop_id = p.scop_id
WHERE
    p.max_tf < 40.0
    AND e.resolution < 2.0;

