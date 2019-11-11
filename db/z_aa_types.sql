/*
SELECT
    string_agg(name, ',') AS names, 
    string_agg(hydrophobicity::VARCHAR, ',') AS hydros,
    polarity, acidic, not_acidic, basic, not_basic, aromatic, not_aromatic, aliphatic, not_aliphatic, sulfur, not_sulfur, hydroxyl, not_hydroxyl, rigid, not_rigid, flexible, not_flexible
FROM
    aa_type
GROUP BY
    polarity, acidic, not_acidic, basic, not_basic, aromatic, not_aromatic, aliphatic, not_aliphatic, sulfur, not_sulfur, hydroxyl, not_hydroxyl, rigid, not_rigid, flexible, not_flexible;
*/

SELECT 
    name,
    three_code,
    one_code,
    CASE WHEN polarity = 'polar' THEN 1 ELSE 0 END AS polar, 
    CASE WHEN polarity = 'nonpolar' THEN 1 ELSE 0 END AS not_polar, 
    acidic, 
    not_acidic, 
    basic, 
    not_basic, 
    aromatic, 
    not_aromatic, 
    aliphatic, 
    not_aliphatic, 
    sulfur, 
    not_sulfur, 
    hydroxyl, 
    not_hydroxyl, 
    flexible, 
    not_flexible,
    rigid, 
    not_rigid
FROM
    aa_type
ORDER BY
    name;
