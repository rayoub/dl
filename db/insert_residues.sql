
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
        max_tf,
        ssa,
        sse,
        descriptor,
        phi,
        psi,
        phi_x,
        phi_y,
        psi_x,
        psi_y
    )
	SELECT
        scop_id,
        pdb_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code, 
        max_tf,
        ssa,
        sse,
        descriptor,
        phi,
        psi,
        phi_x,
        phi_y,
        psi_x,
        psi_y
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

