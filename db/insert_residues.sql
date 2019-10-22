
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
        descriptor,
        ca_x,
        ca_y, 
        ca_z, 
        cb_x, 
        cb_y,
        cb_z
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
        descriptor,
        ca_x,
        ca_y, 
        ca_z, 
        cb_x, 
        cb_y,
        cb_z
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

