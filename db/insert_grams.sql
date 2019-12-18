
CREATE OR REPLACE FUNCTION insert_grams (p_tab gram ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO gram (
        scop_id,
        pdb_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code_1, 
        residue_code_2, 
        residue_code_3,
        max_tf,
        phi,
        psi,
        ss8,
        ss3,
        descriptor
    )
	SELECT
        scop_id,
        pdb_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code_1, 
        residue_code_2, 
        residue_code_3,
        max_tf,
        phi,
        psi,
        ss8,
        ss3,
        descriptor
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

