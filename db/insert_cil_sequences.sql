
CREATE OR REPLACE FUNCTION insert_cil_sequences (p_tab cil_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO cil_sequence (
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

