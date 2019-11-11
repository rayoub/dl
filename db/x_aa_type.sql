
TRUNCATE aa_type;

COPY aa_type (
    name,
    three_code,
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
) FROM '/home/ayoub/git/dl/data/aa_types.txt' WITH (DELIMITER ',');




