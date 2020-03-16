
CREATE TABLE gram_probs AS
WITH calc_counts AS
(
    SELECT
        descriptor,
        residue_code_1 || residue_code_2 || residue_code_3 AS gram,
        COUNT(*) AS group_count
    FROM
        valid_gram
    GROUP BY 
        descriptor,
        residue_code_1,
        residue_code_2, 
        residue_code_3
),
calc_counts_2 AS
(
    SELECT
        descriptor,
        gram,
        group_count,
        ROW_NUMBER() OVER (PARTITION BY descriptor ORDER BY group_count DESC, gram) AS group_rank,
        SUM(group_count) OVER (PARTITION BY descriptor) AS group_sum
    FROM    
        calc_counts
),
calc_probs AS
(
    SELECT
        descriptor,
        gram,
        group_count,
        group_rank,
        group_sum,
        LEAST(0.999, (group_count::REAL / group_sum)::REAL) AS group_prob
    FROM
        calc_counts_2
)
SELECT * FROM calc_probs ORDER BY descriptor, gram;


