
CREATE TABLE gram_descr_counts AS
SELECT
    gram,
    descriptor,
    COUNT(*)
FROM
    angles
GROUP BY
    gram,
    descriptor;
