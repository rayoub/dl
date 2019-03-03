
CREATE OR REPLACE FUNCTION get_sequence_split (p_split_index INTEGER, p_split_count INTEGER)
RETURNS TABLE (
    scop_id VARCHAR
)
AS $$
BEGIN

    RETURN QUERY
    WITH numbered_sequence AS
    (
        SELECT
            ROW_NUMBER() OVER (ORDER BY s.scop_id) AS n,
            s.scop_id
        FROM
            sequence s
    )
    SELECT 
        ns.scop_id
    FROM 
        numbered_sequence ns
    WHERE 
        ns.n % p_split_count = p_split_index 
    ORDER BY 
        ns.scop_id;

END;
$$LANGUAGE plpgsql;


