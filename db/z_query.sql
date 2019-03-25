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
    s.scop_id = 'd2uzsa_';
*/

    WITH indices AS
    (
        SELECT
            r.scop_id,
            r.residue_number AS structure_number,
            s.residue_number_2 AS sequence_number,
            m.residue_number_2 AS map_number
            --s.residue_number_1 AS sequence_number,
            --m.residue_number_1 AS map_number
        FROM
            residue r
            INNER JOIN sequence s
                ON r.order_number = 1 AND r.scop_id = s.scop_id 
            INNER JOIN map m
                ON s.pdb_id = m.pdb_id AND s.chain = m.chain 
    )
    SELECT
        i.scop_id,
        i.structure_number,
        i.sequence_number,
        i.map_number 
    FROM
        indices i
    WHERE
        1 = 1
        AND i.sequence_number IS NULL
        AND i.structure_number != i.map_number
    ORDER BY
        scop_id;


