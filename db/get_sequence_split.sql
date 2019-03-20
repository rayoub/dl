
CREATE OR REPLACE FUNCTION get_sequence_split (p_split_index INTEGER, p_split_count INTEGER)
RETURNS TABLE (
    scop_id VARCHAR,
    residue_number INTEGER,
    insert_code VARCHAR,
    sequence_text VARCHAR,
    map_text VARCHAR
)
AS $$
BEGIN

    RETURN QUERY
    WITH numbered_sequence AS
    (
        SELECT
            ROW_NUMBER() OVER (ORDER BY s.scop_id) AS n,
            s.scop_id,
            s.residue_number_1 AS residue_number,
            s.insert_code_1 AS insert_code,
            s.text AS sequence_text,
            m.text AS map_text
        FROM
            sequence s
            INNER JOIN map m
                ON m.pdb_id = s.pdb_id AND m.chain = s.chain
    )
    SELECT 
        ns.scop_id,
        ns.residue_number,
        ns.insert_code,
        ns.sequence_text,
        ns.map_text
    FROM 
        numbered_sequence ns
    WHERE 
        ns.n % p_split_count = p_split_index 
    ORDER BY 
        ns.scop_id;

END;
$$LANGUAGE plpgsql;


