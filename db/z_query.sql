
/*
SELECT
    s.scop_id,
    s.residue_number_1,
    s.insert_code_1,
    s.residue_number_2,
    s.insert_code_2,
    s.text,
    s.len,
    m.residue_number_1,
    m.insert_code_1,
    m.residue_number_2,
    m.insert_code_2,
    m.text,
    m.len
FROM
    sequence s
    INNER JOIN map m
        ON s.pdb_id = m.pdb_id
        AND s.chain = m.chain
WHERE
    s.scop_id = 'd1gw9a_';
*/

-- write a query that gets all the mismatches start indices where missing in pdb first line
-- then check the matched sequences such that the residue number is null
--

WITH indices AS
(
    SELECT
        r.scop_id,
        r.residue_number AS structure_number,
        s.residue_number_1 AS sequence_number,
        m.residue_number_1 AS map_number
    FROM
        residue r
        INNER JOIN sequence s
            ON r.order_number = 1 AND r.scop_id = s.scop_id 
        INNER JOIN map m
            ON s.pdb_id = m.pdb_id AND s.chain = m.chain 
)
SELECT
    *
FROM
    indices
WHERE
    1 = 1
    AND sequence_number IS NULL
    AND structure_number != map_number
ORDER BY
    scop_id;


