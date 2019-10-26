
CREATE OR REPLACE FUNCTION insert_cir_sequences (p_tab cir_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO cir_sequence (
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

