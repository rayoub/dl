
CREATE OR REPLACE FUNCTION insert_pairs (p_tab pair ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO pair (
        scop_id,
        pdb_id,
        max_tf,
        descriptor_1,
        descriptor_2
    )
	SELECT
        scop_id,
        pdb_id,
        max_tf,
        descriptor_1,
        descriptor_2
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

