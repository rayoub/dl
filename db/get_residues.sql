
CREATE OR REPLACE FUNCTION get_residues (p_scop_id VARCHAR)
RETURNS TABLE (
    scop_id VARCHAR,
    order_number INTEGER,
    residue_number INTEGER,
    insert_code VARCHAR,
    residue_code VARCHAR,
    ssa VARCHAR,
    sse VARCHAR,
    phi NUMERIC,
    psi NUMERIC,
    descriptor VARCHAR,
    ca_x NUMERIC,
    ca_y NUMERIC,
    ca_z NUMERIC,
    cb_x NUMERIC,
    cb_y NUMERIC,
    cb_z NUMERIC
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
        r.sse,
        r.phi,
        r.psi,
        r.descriptor,
        r.ca_x,
        r.ca_y,
        r.ca_z,
        r.cb_x,
        r.cb_y,
        r.cb_z
    FROM 
        residue r
    WHERE
        r.scop_id = p_scop_id
    ORDER BY
        r.order_number;

END;
$$LANGUAGE plpgsql;

