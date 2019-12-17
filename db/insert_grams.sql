
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
        phi,
        psi,
        descriptor,
        max_tf,

        residue_code_1, 
        max_tf_1,
        ss8_1,
        ss3_1,

        residue_code_2, 
        max_tf_2,
        ss8_2,
        ss3_2,

        residue_code_3, 
        max_tf_3,
        ss8_3,
        ss3_3
    )
	SELECT
        scop_id,
        pdb_id,
        order_number,
        residue_number, 
        insert_code, 
        phi,
        psi,
        descriptor,
        max_tf,

        residue_code_1, 
        max_tf_1,
        ss8_1,
        ss3_1,
   
        residue_code_2, 
        max_tf_2,
        ss8_2,
        ss3_2,
    
        residue_code_3, 
	    max_tf_3,
        ss8_3,
        ss3_3
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

