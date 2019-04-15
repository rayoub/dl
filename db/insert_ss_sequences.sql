
CREATE OR REPLACE FUNCTION insert_ss_sequences (p_tab ss_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO ss_sequence (
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

