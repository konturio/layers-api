--liquibase formatted sql

--changeset layers-api-migrations:v1.9/alter-layers-add-tile-size.sql runOnChange:false

ALTER TABLE layers
    ADD COLUMN IF NOT EXISTS tile_size INTEGER;

CREATE OR REPLACE FUNCTION trigger_layers_tile_size_default_value()
    RETURNS trigger
    LANGUAGE plpgsql AS
'
    BEGIN
        NEW.tile_size := CASE NEW.type
                             WHEN ''raster'' THEN 256
                             WHEN ''vector'' THEN 512
                             WHEN ''tiles'' THEN 512
            END;
        RETURN NEW;
    END
';

CREATE OR REPLACE TRIGGER tile_size_default_value
    BEFORE INSERT
    ON layers
    FOR EACH ROW
    WHEN (NEW.tile_size IS NULL AND NEW.type IS NOT NULL)
EXECUTE PROCEDURE trigger_layers_tile_size_default_value();

UPDATE layers
SET tile_size=256
WHERE type = 'raster';

UPDATE layers
SET tile_size=512
WHERE type = 'vector'
   OR type = 'tiles';

UPDATE layers
SET tile_size=512
WHERE public_id = 'openaerialmap';