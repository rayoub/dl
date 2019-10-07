
WITH filter AS
(
    SELECT 
        e.pdb_id,
        e.resolution
    FROM
        entries e
    WHERE
        e.resolution < 1.5
)
SELECT
    r.ck_x,
    r.ck_y,
    r.ck_z,
    r.phi,
    r.psi
FROM
    residue r
    INNER JOIN filter f
        ON r.pdb_id = f.pdb_id
WHERE
    1 = 1
    AND r.ck_x IS NOT NULL
    AND r.ck_y IS NOT NULL
    AND r.ck_z IS NOT NULL
    AND r.phi IS NOT NULL
    AND r.psi IS NOT NULL
ORDER BY
    RANDOM()
LIMIT 10000;
