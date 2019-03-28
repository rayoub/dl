
CREATE OR REPLACE FUNCTION insert_sequence_assignments (p_tab sequence_assignments ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO sequence_assignments (
        scop_id, 
        text
    )
	SELECT
        scop_id,
        text
    FROM
        UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;
