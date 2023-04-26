--liquibase formatted sql

--changeset layers-api-migrations:v1.14/alter-layers-and-apps-layers-add-styles.sql runOnChange:false

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS legend_style_config jsonb;
ALTER TABLE apps_layers
    RENAME COLUMN style_rule TO legend_style_config;

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS map_style_config jsonb;
ALTER TABLE apps_layers
    ADD COLUMN IF NOT EXISTS map_style_config jsonb;

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS popup_config jsonb;
ALTER TABLE apps_layers
    ADD COLUMN IF NOT EXISTS popup_config jsonb;
