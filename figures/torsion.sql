WITH sample AS
(
    SELECT
        phi,
        psi,
        ss3_2 AS ss3
    FROM
        gram g
        INNER JOIN entries e
            ON  e.pdb_id = g.pdb_id
    WHERE
        g.max_tf <  40.0
        AND e.resolution < 2.0
    ORDER BY
        RANDOM()
)
SELECT
    phi,
    psi,
    ss3
FROM
    sample
LIMIT 10000;
 

