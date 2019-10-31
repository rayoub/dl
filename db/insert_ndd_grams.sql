
CREATE OR REPLACE FUNCTION insert_ndd_grams(p_db_id VARCHAR, p_grams INTEGER ARRAY)
RETURNS VOID
AS $$
BEGIN

    INSERT INTO ndd_grams (db_id, grams)
    VALUES (p_db_id, p_grams);

END;
$$ LANGUAGE plpgsql;
