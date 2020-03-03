
CREATE OR REPLACE FUNCTION insert_targets (p_tab target ARRAY)
RETURNS VOID
AS $$
BEGIN

	INSERT INTO target (
        target_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code, 
        ss3,
        ss8,
        phi,
        psi,
        descriptor
    )
	SELECT
        target_id,
        order_number,
        residue_number, 
        insert_code, 
        residue_code, 
        ss3,
        ss8,
        phi,
        psi,
        descriptor
	FROM
		UNNEST(p_tab);

END;
$$ LANGUAGE plpgsql;

