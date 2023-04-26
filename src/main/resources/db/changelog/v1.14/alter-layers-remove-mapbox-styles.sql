--liquibase formatted sql

--changeset layers-api-migrations:v1.14/alter-layers-remove-mapbox-styles.sql runOnChange:false

ALTER TABLE layers DROP COLUMN mapbox_styles;