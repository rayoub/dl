
CREATE OR REPLACE FUNCTION get_residues (p_scop_id VARCHAR)
RETURNS TABLE (
    scop_id VARCHAR,
    order_number INTEGER,
    residue_number INTEGER,
    insert_code VARCHAR,
    residue_code VARCHAR,
    ssa VARCHAR,
    phi DOUBLE PRECISION,
    psi DOUBLE PRECISION,
    phi_x DOUBLE PRECISION,
    phi_y DOUBLE PRECISION,
    psi_x DOUBLE PRECISION,
    psi_y DOUBLE PRECISION,
    phil_x DOUBLE PRECISION,
    phir_x DOUBLE PRECISION,
    spl_x DOUBLE PRECISION,
    spl_y DOUBLE PRECISION,
    spl_z DOUBLE PRECISION,
    spr_x DOUBLE PRECISION,
    spr_y DOUBLE PRECISION,
    spr_z DOUBLE PRECISION
)
AS $$
BEGIN

    RETURN QUERY
    SELECT
        r.scop_id,
        r.order_number,
        r.residue_number,
        COALESCE(r.insert_code,'') AS insert_code,
        r.residue_code,
        r.ssa,
        r.phi,
        r.psi,
        r.phi_x,
        r.phi_y,
        r.psi_x,
        r.psi_y,
        r.phil_x,
        r.phir_x,
        r.spl_x,
        r.spl_y,
        r.spl_z,
        r.spr_x,
        r.spr_y,
        r.spr_z
    FROM 
        residue r
    WHERE
        r.scop_id = p_scop_id
    ORDER BY
        r.order_number;

END;
$$LANGUAGE plpgsql;

