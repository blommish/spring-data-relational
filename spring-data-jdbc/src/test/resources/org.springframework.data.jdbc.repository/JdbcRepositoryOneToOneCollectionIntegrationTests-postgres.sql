DROP TABLE dummy_entity;
CREATE TABLE dummy_entity
(
    id   UUID PRIMARY KEY,
    test VARCHAR(100)
);
DROP TABLE dummy_entity2;
CREATE TABLE dummy_entity2
(
    id              BIGSERIAL PRIMARY KEY,
    dummy_entity_id UUID NOT NULL REFERENCES dummy_entity (id)
);
