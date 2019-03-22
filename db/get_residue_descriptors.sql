
CREATE OR REPLACE FUNCTION get_residue_descriptors (p_scop_id VARCHAR)
RETURNS TABLE (
    scop_id VARCHAR,
    residue_number INTEGER,
    insert_code VARCHAR,
    residue_code VARCHAR,
    descriptor VARCHAR
)
AS $$
BEGIN

    RETURN QUERY
    SELECT
        r.scop_id,
        r.residue_number,
        COALESCE(r.insert_code,'') AS insert_code,
        r.residue_code,
        r.descriptor
    FROM 
        residue r
    WHERE
        r.scop_id = p_scop_id
    ORDER BY
        r.order_number;

END;
$$LANGUAGE plpgsql;

