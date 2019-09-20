
CREATE OR REPLACE FUNCTION insert_ck_sequences (p_tab ck_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO ck_sequence (
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

