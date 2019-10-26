
/* 
 * when producing training items separate the fields by bars '|' using \f '|' command in psql
 * also get rid of alignment with \a command
*/

SELECT
    aa.text,
    pp.seq,
    pp.weights,
    spl.seq,
    spl.weights,
    spr.seq,
    spr.weights,
    ci.seq,
    ci.weights,
    cil.seq,
    cil.weights,
    cir.seq,
    cir.weights
FROM
    aa_sequence aa
    INNER JOIN pp_sequence pp
        ON pp.scop_id = aa.scop_id
    INNER JOIN spl_sequence spl
        ON spl.scop_id = aa.scop_id
    INNER JOIN spr_sequence spr
        ON spr.scop_id = aa.scop_id
    INNER JOIN ci_sequence ci
        ON ci.scop_id = aa.scop_id
    INNER JOIN cil_sequence cil
        ON cil.scop_id = aa.scop_id
    INNER JOIN cir_sequence cir
        ON cir.scop_id = aa.scop_id
    INNER JOIN astral_40 a40
        ON a40.scop_id = aa.scop_id
WHERE
    aa.len < 400
ORDER BY
    a40.r;




