
CREATE OR REPLACE FUNCTION insert_maps (p_tab map ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO map (
        pdb_id,
        chain,
        residue_number_1,
        insert_code_1,
        residue_number_2,
        insert_code_2,
        text
    )
	SELECT
        pdb_id,
        chain,
        residue_number_1,
        insert_code_1,
        residue_number_2,
        insert_code_2,
        text
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

