
CREATE OR REPLACE FUNCTION insert_pp_sequences (p_tab pp_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO pp_sequence (
        scop_id, 
        text,
        len,
        missing_len
    )
	SELECT
        scop_id,
        text,
        len,
        missing_len
    FROM
        UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

