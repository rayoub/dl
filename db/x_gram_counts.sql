
CREATE TABLE gram_counts AS
WITH basic_counts AS
(
    SELECT
        residue_code_1,
        residue_code_2, 
        residue_code_3,
        descriptor,
        COUNT(*) AS basic_count
    FROM
        valid_gram
    GROUP BY 
        CUBE(residue_code_1, residue_code_2, residue_code_3, descriptor)
),
group_counts AS
(
    SELECT
        '_'
        ||
        CASE WHEN residue_code_1 IS NOT NULL THEN 'r1' ELSE '' END 
        ||
        CASE WHEN residue_code_2 IS NOT NULL THEN 'r2' ELSE '' END 
        ||
        CASE WHEN residue_code_3 IS NOT NULL THEN 'r3' ELSE '' END 
        ||
        CASE WHEN descriptor IS NOT NULL THEN 'd' ELSE '' END AS group_set,
        residue_code_1,
        residue_code_2,
        residue_code_3,
        descriptor,
        basic_count AS group_count
    FROM
        basic_counts
)
SELECT
    group_set,
    residue_code_1,
    residue_code_2,
    residue_code_3,
    descriptor,
    group_count
FROM
    group_counts;




