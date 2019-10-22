
WITH cte AS 
(
    SELECT
        residue_code,
        CASE WHEN phi <= -30 OR phi >= 150 THEN 1 ELSE 0 END AS less_than,
        CASE WHEN phi > -30 AND phi < 150 THEN 1 ELSE 0 END AS greater_than
    FROM
        residue
    WHERE 
        phi IS NOT NULL AND psi IS NOT NULL
        AND residue_code NOT IN ('?','U')
    ORDER BY
        residue_code
),
summed_up AS
(
    SELECT 
        residue_code,
        COUNT(*) AS total_count,
        SUM(less_than) AS less_than_count,
        SUM(greater_than) AS greater_than_count
    FROM
        cte
    GROUP BY
        residue_code
    ORDER BY
        residue_code
),
sums AS
(
    SELECT
        residue_code,
        total_count,
        less_than_count,
        greater_than_count,
        less_than_count / total_count::NUMERIC AS ltpct,
        greater_than_count / total_count::NUMERIC AS gtpct
    FROM
        summed_up
)
SELECT SUM(less_than_count) / SUM(total_count)  FROM sums;


--WITH cte AS
--(
--    SELECT
--        phi,
--        FLOOR((phi / 10))::INTEGER AS phi_div
--    FROM
--        residue
--    WHERE
--        residue_code = 'G'
--        AND phi IS NOT NULL
--    ORDER BY
--        phi
--)
--SELECT
--    phi_div,
--    COUNT(*)
--FROM
--    cte
--GROUP BY phi_div
--ORDER BY phi_div;


