--changeset layers-api-migrations:alter-layers-add-is-global.sql runOnChange:false

ALTER TABLE layers ADD COLUMN IF NOT EXISTS is_global boolean default false;