
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
        ss3_1,
        ss3_2,
        ss3_3,
        ss8_1,
        ss8_2,
        ss8_3,
        max_tf,
        phi,
        psi,
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
        ss3_1,
        ss3_2,
        ss3_3,
        ss8_1,
        ss8_2,
        ss8_3,
        max_tf,
        phi,
        psi,
        descriptor
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

