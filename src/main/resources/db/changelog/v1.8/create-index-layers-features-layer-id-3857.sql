CREATE INDEX IF NOT EXISTS layers_features_layer_id_3857_idx ON layers_features USING gist(layer_id, ST_Transform(geom, 3857));
