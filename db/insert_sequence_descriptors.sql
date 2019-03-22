
CREATE OR REPLACE FUNCTION insert_sequence_descriptors (p_tab sequence_descriptors ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO sequence_descriptors (
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

