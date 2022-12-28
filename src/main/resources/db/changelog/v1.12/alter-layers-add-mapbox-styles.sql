--liquibase formatted sql

--changeset layers-api-migrations:v1.12/alter-layers-add-mapbox-styles.sql runOnChange:false

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS mapbox_styles jsonb CHECK (mapbox_styles ?? 'url');
