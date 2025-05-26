--liquibase formatted sql

--changeset layers-api-migrations:create-layer-access-table.sql runOnChange:false

CREATE TABLE IF NOT EXISTS layers_access
(
    layer_id  text NOT NULL REFERENCES layers (public_id) ON DELETE CASCADE,
    user_name text NOT NULL,
    PRIMARY KEY (layer_id, user_name)
);
