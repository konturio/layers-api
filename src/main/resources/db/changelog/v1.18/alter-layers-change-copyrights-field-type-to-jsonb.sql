--liquibase formatted sql

--changeset layers-api-migrations:v1.18/alter-layers-change-copyrights-field-type-to-jsonb.sql runOnChange:false

ALTER TABLE layers 
ALTER COLUMN copyrights SET DATA TYPE jsonb 
USING CASE 
    WHEN copyrights IS NULL THEN '[]'::jsonb 
    ELSE TO_JSONB(copyrights::text) 
END;
