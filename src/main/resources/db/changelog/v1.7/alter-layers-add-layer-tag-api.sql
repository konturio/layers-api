--changeset layers-api-migrations:alter-layers-add-layer-tag-api.sql runOnChange:false

ALTER TABLE layers ADD COLUMN IF NOT EXISTS api_tag character varying;
ALTER TABLE layers ADD COLUMN IF NOT EXISTS api_key character varying;