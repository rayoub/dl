
CREATE OR REPLACE FUNCTION insert_spl_sequences (p_tab spl_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO spl_sequence (
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

