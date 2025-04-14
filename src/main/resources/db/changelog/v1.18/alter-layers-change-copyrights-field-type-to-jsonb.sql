--liquibase formatted sql

--changeset layers-api-migrations:v1.18/alter-layers-change-copyrights-field-type-to-jsonb.sql runOnChange:false runInTransaction:false

ALTER TABLE layers 
ALTER COLUMN copyrights SET DATA TYPE jsonb 
USING CASE 
    WHEN copyrights IS NULL THEN '[]'::jsonb 
    ELSE jsonb_build_array(copyrights::text) 
END;
