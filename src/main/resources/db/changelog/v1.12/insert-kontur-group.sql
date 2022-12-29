--liquibase formatted sql

--changeset layers-api-migrations:v1.12/insert-kontur-group.sql runOnChange:false

INSERT INTO layers_group_properties (name, is_opened, mutually_exclusive)
VALUES ('Kontur', true, true)
ON CONFLICT (name) DO NOTHING;