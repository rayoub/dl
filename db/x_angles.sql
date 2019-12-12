
CREATE TABLE angles AS
SELECT
    gram,
    descriptor,
    phi,
    psi
FROM
    residue r
    INNER JOIN entries e
        ON e.pdb_id = r.pdb_id
WHERE
    gram IS NOT NULL
    AND descriptor IS NOT NULL
    AND phi IS NOT NULL 
    AND psi IS NOT NULL
    AND max_tf < 40.0
    AND resolution < 2.0;

