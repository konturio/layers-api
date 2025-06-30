--liquibase formatted sql

--changeset layers-api-migrations:v1.20/create-index-layers-features-layer-id.sql runOnChange:true

CREATE INDEX IF NOT EXISTS layers_features_layer_id_idx ON layers_features USING btree(layer_id);
