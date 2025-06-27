--liquibase formatted sql

--changeset layers-api-migrations:v1.19/create-index-layers-features-layer-resolution.sql runOnChange:true

CREATE INDEX IF NOT EXISTS layers_features_layer_resolution_idx
ON layers_features (
  layer_id,
  ((properties ->> 'resolution')::int)
);
