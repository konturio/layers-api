--liquibase formatted sql

--changeset layers-api-migrations:alter-layers-add-feature-properties.sql runOnChange:false

ALTER TABLE layers ADD COLUMN IF NOT EXISTS feature_properties jsonb;