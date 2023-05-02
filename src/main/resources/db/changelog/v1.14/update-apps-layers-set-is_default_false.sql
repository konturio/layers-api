--liquibase formatted sql

--changeset layers-api-migrations:v1.14/update-apps-layers-set-is_default_false.sql runOnChange:false

update apps_layers
    set is_default = false
    where layer_id = 'BIV__Kontur OpenStreetMap Quantity'
        and app_id in ('58851b50-9574-4aec-a3a6-425fa18dcb54', 'f70488c2-055c-4599-a080-ded10c47730f');
