
SELECT
    STRING_AGG(res, ',' ORDER BY num) AS text,
    STRING_AGG(ss, ',' ORDER BY num) AS text
FROM
    benchmark
WHERE
    name = 'CASP11'
GROUP BY
    model
ORDER BY
    model;
