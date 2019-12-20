
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
        AS group_set,
        '_'
        || 
        CASE WHEN residue_code_1 IS NOT NULL THEN residue_code_1 ELSE '_' END 
        ||
        CASE WHEN residue_code_2 IS NOT NULL THEN residue_code_2 ELSE '_' END 
        ||
        CASE WHEN residue_code_3 IS NOT NULL THEN residue_code_3 ELSE '_' END 
        AS group_id,
        residue_code_1,
        residue_code_2,
        residue_code_3,
        descriptor,
        basic_count AS group_count
    FROM
        basic_counts
    WHERE
        descriptor IS NOT NULL
),
group_counts_2 AS
(
    SELECT
        group_set,
        group_id,
        residue_code_1,
        residue_code_2,
        residue_code_3,
        descriptor,
        ROW_NUMBER() OVER (PARTITION BY group_set, residue_code_1, residue_code_2, residue_code_3 ORDER BY group_count DESC, descriptor) AS group_rank,
        group_count,
        SUM(group_count) OVER (PARTITION BY group_set, residue_code_1, residue_code_2, residue_code_3) AS group_sum
    FROM
        group_counts
)
SELECT
        group_set,
        group_id,
        residue_code_1,
        residue_code_2,
        residue_code_3,
        descriptor,
        group_rank,
        group_count,
        group_sum,
        (group_count::REAL / group_sum)::REAL AS group_prob
FROM
    group_counts_2;

