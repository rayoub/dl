
CREATE TABLE gram_counts AS
WITH counts AS
(
    SELECT
        residue_code_1,
        residue_code_2, 
        residue_code_3,
        descriptor,
        COUNT(*) AS count
    FROM
        valid_gram
    GROUP BY 
        CUBE(residue_code_1, residue_code_2, residue_code_3, descriptor)
)
SELECT
    '_'
    ||
    CASE WHEN residue_code_1 IS NOT NULL THEN 'r1' ELSE '' END 
    ||
    CASE WHEN residue_code_2 IS NOT NULL THEN 'r2' ELSE '' END 
    ||
    CASE WHEN residue_code_3 IS NOT NULL THEN 'r3' ELSE '' END 
    ||
    CASE WHEN descriptor IS NOT NULL THEN 'd' ELSE '' END AS grouping_set,
    residue_code_1,
    residue_code_2,
    residue_code_3,
    descriptor,
    count
FROM
    counts;




