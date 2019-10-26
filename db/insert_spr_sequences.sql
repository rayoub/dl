
CREATE OR REPLACE FUNCTION insert_spr_sequences (p_tab spr_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO spr_sequence (
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

