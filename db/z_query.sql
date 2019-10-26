
SELECT
    CASE 
        WHEN ssa IN ('G','H','I','T') THEN 'Helix'
        WHEN ssa IN ('E','B') THEN 'Strand'
        ELSE 'Loop'
    END AS sse,
    phi_x,
    phi_y,
    psi_x,
    psi_y,
    CASE WHEN phil_x IS NULL THEN 0 ELSE phil_x END AS phil_x,
    CASE WHEN phir_x IS NULL THEN 0 ELSE phir_x END AS phir_x,
    residue_code As rc
FROM
    residue 
WHERE
    phi_x IS NOT NULL AND phi_y IS NOT NULL AND psi_x IS NOT NULL AND psi_y IS NOT NULL
ORDER BY RANDOM()
LIMIT 1000;
