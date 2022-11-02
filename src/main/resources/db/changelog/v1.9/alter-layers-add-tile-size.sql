--liquibase formatted sql

--changeset layers-api-migrations:v1.9/alter-layers-add-tile-size.sql runOnChange:false

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS tile_size INTEGER;