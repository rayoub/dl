
CREATE OR REPLACE FUNCTION insert_ci_sequences (p_tab ci_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO ci_sequence (
        scop_id, 
        seq,
        weights,
        len,
        missing_len
    )
	SELECT
        scop_id,
        seq,
        weights,
        len,
        missing_len
    FROM
        UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

