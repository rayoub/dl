
/* 
 * when producing training items separate the fields by bars '|' using \f '|' command in psql
 * also get rid of alignment with \a command
*/

SELECT
    aa.text,
    pp.seq,
    pp.weights,
    sp.seq,
    sp.weights,
    ci.seq,
    ci.weights
FROM
    aa_sequence aa
    INNER JOIN pp_sequence pp
        ON pp.scop_id = aa.scop_id
    INNER JOIN sp_sequence sp
        ON sp.scop_id = aa.scop_id
    INNER JOIN ci_sequence ci
        ON ci.scop_id = aa.scop_id
    INNER JOIN astral_40 a40
        ON a40.scop_id = aa.scop_id
WHERE
    aa.len < 400
ORDER BY
    a40.r;




