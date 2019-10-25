
CREATE OR REPLACE FUNCTION insert_residues (p_tab residue ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO residue (
        scop_id,
        pdb_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code, 
        ssa,
        phi,
        psi,
        phi_x,
        phi_y,
        psi_x,
        psi_y,
        sp_x, 
        sp_y,
        sp_z
    )
	SELECT
        scop_id,
        pdb_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code, 
        ssa,
        phi,
        psi,
        phi_x,
        phi_y,
        psi_x,
        psi_y,
        sp_x, 
        sp_y,
        sp_z
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

