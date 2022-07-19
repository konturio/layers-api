--liquibase formatted sql

--changeset layers-api-migrations:insert-groups-and-categories.sql runOnChange:false

INSERT INTO layers_group_properties (name, is_opened, mutually_exclusive)
VALUES ('other', false, false),
       ('user_layers', false, false),
       ('layersInSelectedArea', false, false)
ON CONFLICT (name) DO NOTHING;

INSERT INTO layers_category_properties (name, is_opened, mutually_exclusive)
VALUES ('overlay', false, false),
       ('base', false, false)
ON CONFLICT (name) DO NOTHING;