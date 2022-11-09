--liquibase formatted sql

--changeset layers-api-migrations:v1.10/alter-apps-add-name-and-icon-url.sql runOnChange:false

ALTER TABLE apps
    ADD COLUMN IF NOT EXISTS name TEXT,
    ADD COLUMN IF NOT EXISTS icon_url TEXT;