
CREATE TABLE valid_gram AS
SELECT
    g.*
FROM
    gram g
    INNER JOIN entries e
        ON e.pdb_id = g.pdb_id
WHERE
    g.max_tf < 40.0
    AND e.resolution < 2.0;

