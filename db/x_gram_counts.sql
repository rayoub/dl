
CREATE TABLE gram_counts AS
SELECT
    gram,
    COUNT(*)
FROM
    angles
GROUP BY
    gram;
