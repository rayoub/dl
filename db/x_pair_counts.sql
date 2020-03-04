
CREATE TABLE pair_counts AS
WITH basic_counts AS
(
    SELECT
        descriptor_1,
        descriptor_2,
        COUNT(*) AS basic_count
    FROM
        valid_pair
    GROUP BY 
        CUBE(descriptor_1, descriptor_2)
),
group_counts AS
(
    SELECT
        '_'
        || 
        CASE WHEN descriptor_1 IS NOT NULL THEN 'd1' ELSE '' END 
        AS group_set,
        '_'
        || 
        CASE WHEN descriptor_1 IS NOT NULL THEN descriptor_1 ELSE '_' END 
        AS group_id,
        descriptor_1,
        descriptor_2,
        basic_count AS group_count
    FROM
        basic_counts
    WHERE
        descriptor_2 IS NOT NULL
),
group_counts_2 AS
(
    SELECT
        group_set,
        group_id,
        descriptor_1,
        descriptor_2,
        ROW_NUMBER() OVER (PARTITION BY group_set, descriptor_1 ORDER BY group_count DESC, descriptor_2) AS group_rank,
        group_count,
        SUM(group_count) OVER (PARTITION BY group_set, descriptor_1) AS group_sum
    FROM
        group_counts
)
SELECT
        group_set,
        group_id,
        descriptor_1,
        descriptor_2,
        group_rank,
        group_count,
        group_sum,
        (group_count::REAL / group_sum)::REAL AS group_prob
FROM
    group_counts_2;

