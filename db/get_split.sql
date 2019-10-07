
CREATE OR REPLACE FUNCTION get_split (p_split_index INTEGER, p_split_count INTEGER)
RETURNS TABLE (
    scop_id VARCHAR,
    pdb_id VARCHAR,
    residue_number_1 INTEGER,
    insert_code_1 VARCHAR,
    residue_number_2 INTEGER,
    insert_code_2 VARCHAR,
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
            s.pdb_id,
            s.residue_number_1,
            s.insert_code_1,
            s.residue_number_2,
            s.insert_code_2,
            s.text AS sequence_text,
            m.text AS map_text
        FROM
            aa_sequence s
            INNER JOIN map m
                ON m.pdb_id = s.pdb_id AND m.chain = s.chain
    )
    SELECT 
        ns.scop_id,
        ns.pdb_id,
        ns.residue_number_1,
        ns.insert_code_1,
        ns.residue_number_2,
        ns.insert_code_2,
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


