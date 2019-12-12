
WITH angle_meds AS
(
    SELECT
        gram,
        descriptor,
        QUANTILE(phi, 0.5) AS phi,
        QUANTILE(psi, 0.5) AS psi
    FROM
        valid_angles
    GROUP BY
        gram,
        descriptor
)
SELECT
    *
FROM
    angle_meds;






--WITH angle_sums AS
--(
--    SELECT
--        gram,
--        descriptor,
--        SUM(CASE WHEN phi > 0 THEN phi ELSE 0 END) AS phi_p,
--        SUM(CASE WHEN phi <= 0 THEN phi ELSE 0 END) AS phi_n,
--        SUM(CASE WHEN psi > 0 THEN psi ELSE 0 END) AS psi_p,
--        SUM(CASE WHEN psi <= 0 THEN psi ELSE 0 END) AS psi_n
--    FROM
--        valid_angles
--    GROUP BY
--        gram,
--        descriptor
--),
--angle_counts AS
--(
--    SELECT
--        gram,
--        descriptor,
--        SUM(CASE WHEN phi > 0 THEN 1 ELSE 0 END) AS phi_p_c,
--        SUM(CASE WHEN phi <= 0 THEN 1 ELSE 0 END) AS phi_n_c,
--        SUM(CASE WHEN phi > 0 THEN 1 ELSE 0 END) + SUM(CASE WHEN phi <= 0 THEN 1 ELSE 0 END) AS phi_c,
--        SUM(CASE WHEN psi > 0 THEN 1 ELSE 0 END) AS psi_p_c,
--        SUM(CASE WHEN psi <= 0 THEN 1 ELSE 0 END) AS psi_n_c,
--        SUM(CASE WHEN psi > 0 THEN 1 ELSE 0 END) + SUM(CASE WHEN psi <= 0 THEN 1 ELSE 0 END) AS psi_c
--    FROM
--        valid_angles
--    GROUP BY
--        gram,
--        descriptor
--),
--angle_averages_1 AS
--(
--    SELECT 
--        s.gram,
--        s.descriptor,
--        CASE WHEN c.phi_p_c > 0 THEN s.phi_p / c.phi_p_c ELSE 0 END AS phi_p,
--        CASE WHEN c.phi_n_c > 0 THEN s.phi_n / c.phi_n_c ELSE 0 END AS phi_n,
--        CASE WHEN c.psi_p_c > 0 THEN s.psi_p / c.psi_p_c ELSE 0 END AS psi_p,
--        CASE WHEN c.psi_n_c > 0 THEN s.psi_n / c.psi_n_c ELSE 0 END AS psi_n,
--        c.phi_p_c,
--        c.phi_n_c,
--        c.phi_c,
--        c.psi_p_c,
--        c.psi_n_c,
--        c.psi_c
--    FROM
--        angle_sums s
--        LEFT JOIN angle_counts c
--            ON s.gram = c.gram
--            AND s.descriptor = c.descriptor
--),
--adjusted_angles_1 AS
--(
--    SELECT 
--        gram,
--        descriptor,
--        phi_p,
--        CASE WHEN phi_n_c > 0 AND phi_p - phi_n > 180.0 THEN phi_n + 360.0 ELSE phi_n END AS phi_n,
--        psi_p,
--        CASE WHEN psi_n_c > 0 AND psi_p - psi_n > 180.0 THEN psi_n + 360.0 ELSE psi_n END AS psi_n,
--        phi_p_c,
--        phi_n_c,
--        phi_c,
--        psi_p_c,
--        psi_n_c,
--        psi_c
--    FROM
--        angle_averages_1
--),
--angle_averages_2 AS
--(
--    SELECT 
--        gram,
--        descriptor,
--        (phi_p * phi_p_c + phi_n * phi_n_c) / phi_c AS phi,
--        (psi_p * psi_p_c + psi_n * psi_n_c) / psi_c AS psi,
--        phi_p_c,
--        phi_n_c,
--        phi_c,
--        psi_p_c,
--        psi_n_c,
--        psi_c
--    FROM 
--        adjusted_angles_1
--),
--adjusted_angles_2 AS
--(
--    SELECT
--        gram,
--        descriptor,
--        CASE WHEN phi > 180.0 THEN 360 - phi ELSE phi END AS phi,
--        CASE WHEN psi > 180.0 THEN 360 - psi ELSE psi END AS psi,
--        phi_p_c,
--        phi_n_c,
--        phi_c,
--        psi_p_c,
--        psi_n_c,
--        psi_c
--    FROM 
--        angle_averages_2
--)
--SELECT 
--    * 
--FROM adjusted_angles_2;



