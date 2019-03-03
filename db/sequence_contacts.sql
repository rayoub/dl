
CREATE TABLE sequence_contacts
(
    scop_id VARCHAR NOT NULL,
    position INTEGER NOT NULL,
    contacts VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_sequence_contacts_unique ON sequence_contacts (scop_id, position);
