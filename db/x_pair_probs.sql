
CREATE TABLE pair_probs AS
WITH calc_counts AS
(
    SELECT
        descriptor_2,
        descriptor_1,
        COUNT(*) AS group_count
    FROM
        valid_pair
    GROUP BY 
        descriptor_2, 
        descriptor_1
),
calc_counts_2 AS
(
    SELECT
        descriptor_2,
        descriptor_1,
        group_count,
        ROW_NUMBER() OVER (PARTITION BY descriptor_2 ORDER BY group_count DESC, descriptor_1) AS group_rank,
        SUM(group_count) OVER (PARTITION BY descriptor_2) AS group_sum
    FROM
        calc_counts
),
calc_probs AS
(
    SELECT
        descriptor_2,
        descriptor_1,
        group_count,
        group_rank,
        group_sum,
        LEAST(0.999, (group_count::REAL / group_sum)::REAL) AS group_prob
    FROM
        calc_counts_2
)
SELECT * FROM calc_probs ORDER BY descriptor_2, descriptor_1;


