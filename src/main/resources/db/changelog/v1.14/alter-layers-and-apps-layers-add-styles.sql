--liquibase formatted sql

--changeset layers-api-migrations:v1.14/alter-layers-and-apps-layers-add-styles.sql runOnChange:false

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS style_rule jsonb;

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS style jsonb;
ALTER TABLE apps_layers
    ADD COLUMN IF NOT EXISTS style jsonb;

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS popup_config jsonb;
ALTER TABLE apps_layers
    ADD COLUMN IF NOT EXISTS popup_config jsonb;
