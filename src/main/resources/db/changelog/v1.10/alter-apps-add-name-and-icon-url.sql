--liquibase formatted sql

--changeset layers-api-migrations:v1.10/alter-apps-add-name-and-icon-url.sql runOnChange:false

ALTER TABLE apps
    ADD COLUMN IF NOT EXISTS name             TEXT,
    ADD COLUMN IF NOT EXISTS sidebar_icon_url TEXT,
    ADD COLUMN IF NOT EXISTS favicon_url      TEXT;