
CREATE OR REPLACE FUNCTION insert_pairs (p_tab pair ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO pair (
        scop_id,
        pdb_id,
        max_tf,
        residue_code_1,
        residue_code_2,
        descriptor_1,
        descriptor_2
    )
	SELECT
        scop_id,
        pdb_id,
        max_tf,
        residue_code_1,
        residue_code_2,
        descriptor_1,
        descriptor_2
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

