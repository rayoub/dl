
/* 
 * when producing training items separate the fields by bars '|' using \f '|' command in psql
 * also get rid of alignment with \a command
*/

SELECT
    aa.text,
    pp.seq,
    pp.weights,
    ci.seq,
    ci.weights
FROM
    aa_sequence aa
    INNER JOIN pp_sequence pp
        ON pp.scop_id = aa.scop_id
    INNER JOIN ci_sequence ci
        ON ci.scop_id = aa.scop_id
    INNER JOIN astral_95 a95
        ON a95.scop_id = aa.scop_id
WHERE
    aa.len < 400
ORDER BY
    a95.r;




