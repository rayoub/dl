
SELECT
    s.scop_id,
    s.text,
    a.text
FROM
    sequence s
    INNER JOIN sequence_assignments a
        ON a.scop_id = s.scop_id
WHERE
    LENGTH(s.text) = LENGTH(a.text)
LIMIT 1000;
