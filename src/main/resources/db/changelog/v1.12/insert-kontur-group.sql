--liquibase formatted sql

--changeset layers-api-migrations:insert-layers-group.sql runOnChange:true

INSERT INTO layers_group_properties (name, is_opened, mutually_exclusive)
VALUES ('Kontur', true, true)
ON CONFLICT (name) DO NOTHING;