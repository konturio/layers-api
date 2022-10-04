--liquibase formatted sql

--changeset layers-api-migrations:insert-layers-group.sql runOnChange:true

INSERT INTO layers_group_properties (name, is_opened, mutually_exclusive)
VALUES ('bivariate', true, true),
    ('qa', true, false),
    ('osmbasedmap', false, false),
    ('elevation', true, false),
    ('photo', true, false),
    ('map', true, false),
    ('Open Aerial Map', true, false)
ON CONFLICT (name) DO NOTHING;