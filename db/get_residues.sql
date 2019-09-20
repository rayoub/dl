
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
    descriptor VARCHAR,
    ca_x DOUBLE PRECISION,
    ca_y DOUBLE PRECISION,
    ca_z DOUBLE PRECISION,
    cb_x DOUBLE PRECISION,
    cb_y DOUBLE PRECISION,
    cb_z DOUBLE PRECISION,
    ck_x DOUBLE PRECISION,
    ck_y DOUBLE PRECISION,
    ck_z DOUBLE PRECISION
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
        r.descriptor,
        r.ca_x,
        r.ca_y,
        r.ca_z,
        r.cb_x,
        r.cb_y,
        r.cb_z,
        r.ck_x,
        r.ck_y,
        r.ck_z
    FROM 
        residue r
    WHERE
        r.scop_id = p_scop_id
    ORDER BY
        r.order_number;

END;
$$LANGUAGE plpgsql;

