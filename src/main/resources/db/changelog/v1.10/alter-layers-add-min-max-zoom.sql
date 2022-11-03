--liquibase formatted sql

--changeset layers-api-migrations:v1.10/alter-layers-add-min-max-zoom.sql runOnChange:false

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS min_zoom INTEGER,
    ADD COLUMN IF NOT EXISTS max_zoom INTEGER;

UPDATE layers
    SET min_zoom = null,
        max_zoom = null;
