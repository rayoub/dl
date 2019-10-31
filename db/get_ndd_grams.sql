
CREATE OR REPLACE FUNCTION get_ndd_grams (p_db_ids VARCHAR ARRAY)
RETURNS TABLE (
    db_id VARCHAR,
    grams INTEGER ARRAY
)
AS $$
BEGIN

    RETURN QUERY
    SELECT 
        g.db_id,
        g.grams
    FROM
        ndd_grams g
        INNER JOIN UNNEST(p_db_ids) AS ids (db_id)
            ON ids.db_id = g.db_id;

END;
$$LANGUAGE plpgsql;


