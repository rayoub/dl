
SELECT
    aa.text,
    ss.text
FROM
    aa_sequence aa
    INNER JOIN ss_sequence ss
        ON ss.scop_id = aa.scop_id
WHERE
    ss.missing_len = 0
    AND ss.len >= 90
    AND ss.len <= 120
ORDER BY
    RANDOM();

