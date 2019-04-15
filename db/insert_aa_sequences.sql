
CREATE OR REPLACE FUNCTION insert_aa_sequences (p_tab aa_sequence ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO aa_sequence (
        scop_id,
        pdb_id,
        cl,
        cf,
        sf,
        fa,
        chain,
        residue_number_1,
        insert_code_1,
        residue_number_2,
        insert_code_2,
        text,
        len
    )
	SELECT
        scop_id,
        pdb_id,
        cl,
        cf,
        sf,
        fa,
        chain,
        residue_number_1,
        insert_code_1,
        residue_number_2,
        insert_code_2,
        text,
        len
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

