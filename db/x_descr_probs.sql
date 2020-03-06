
CREATE TABLE descr_probs AS
WITH calc_counts AS
(
    SELECT
        residue_code_1 || residue_code_2 || residue_code_3 AS gram,
        descriptor,
        COUNT(*) AS group_count
    FROM
        valid_gram
    GROUP BY 
        residue_code_1,
        residue_code_2, 
        residue_code_3,
        descriptor
),
calc_counts_2 AS
(
    SELECT
        gram,
        descriptor,
        group_count,
        ROW_NUMBER() OVER (PARTITION BY gram ORDER BY group_count DESC, descriptor) AS group_rank,
        SUM(group_count) OVER (PARTITION BY gram) AS group_sum
    FROM    
        calc_counts
),
calc_probs AS
(
    SELECT
        gram,
        descriptor,
        group_count,
        group_rank,
        group_sum,
        LEAST(0.999, (group_count::REAL / group_sum)::REAL) AS group_prob
    FROM
        calc_counts_2
)
SELECT * FROM calc_probs ORDER BY gram, descriptor;


