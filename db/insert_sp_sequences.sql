
CREATE OR REPLACE FUNCTION insert_sp_sequences (p_tab sp_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO sp_sequence (
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

