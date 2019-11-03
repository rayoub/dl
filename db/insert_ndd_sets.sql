CREATE OR REPLACE FUNCTION insert_ndd_sets (p_tab ndd_set ARRAY)
RETURNS VOID
AS $$
BEGIN

    TRUNCATE TABLE ndd_set;

    INSERT INTO ndd_set (pivot_db_id, member_db_id, similarity)
    SELECT
        pivot_db_id,
        member_db_id,
        similarity
    FROM
        UNNEST(p_tab);
               
END;
$$ LANGUAGE plpgsql;
