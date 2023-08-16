--liquibase formatted sql

--changeset layers-api-migrations:v1.15/15721_alter_layers_features_table.sql runOnChange:false

ALTER TABLE layers_features
    ADD CONSTRAINT unique_feature_layer
        UNIQUE (feature_id, layer_id);