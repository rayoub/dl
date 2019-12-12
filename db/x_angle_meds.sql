
CREATE TABLE angle_meds AS
WITH angle_meds AS
(
    SELECT
        gram,
        descriptor,
        QUANTILE(phi, 0.5) AS phi,
        QUANTILE(psi, 0.5) AS psi
    FROM
        angles
    GROUP BY
        gram,
        descriptor
)
SELECT
    gram,
    descriptor,
    phi,
    psi
FROM
    angle_meds;

