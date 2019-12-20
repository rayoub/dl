
CREATE TABLE gram_dists AS
SELECT
    group_id,
    -SUM(group_prob * LOG(2.0, group_prob::NUMERIC))::REAL AS h
FROM
    gram_counts
GROUP BY 
    group_id;




