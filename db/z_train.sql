/*
WITH arrs AS 
(
    SELECT
        STRING_TO_ARRAY(aa.text, ',') AS aa_arr,
        STRING_TO_ARRAY(ss.text, ',') AS ss_arr
    FROM
        aa_sequence aa
        INNER JOIN ss_sequence ss
            ON ss.scop_id = aa.scop_id
    WHERE
        aa.len BETWEEN 90 AND 120
        AND ss.missing_len = 0
)
SELECT
    -- pad arrays and convert back to strings
    ARRAY_TO_STRING(aa_arr::VARCHAR[] || ARRAY_FILL('_'::VARCHAR, ARRAY[200 - CARDINALITY(aa_arr)]), ','),
    ARRAY_TO_STRING(ss_arr::VARCHAR[] || ARRAY_FILL('_'::VARCHAR, ARRAY[200 - CARDINALITY(ss_arr)]), ',')
FROM
    arrs
ORDER BY
    RANDOM();
*/

SELECT
    aa.text,
    ss.text
FROM
    aa_sequence aa
    INNER JOIN ss_sequence ss
        ON ss.scop_id = aa.scop_id
    INNER JOIN astral_40 a40
        ON a40.scop_id = aa.scop_id
WHERE
    ss.missing_len = 0
ORDER BY
    aa.len ASC;




