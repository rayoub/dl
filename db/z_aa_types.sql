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
    one_code,
    polar,
    not_polar,
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
    not_rigid,
    hydrophobicity
FROM
    aa_type
ORDER BY
    one_code;
