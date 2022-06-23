--liquibase formatted sql

--changeset layers-api-migrations:add-apps-and-apps-layers.sql runOnChange:false

CREATE TABLE IF NOT EXISTS apps
(
    id                     uuid NOT NULL DEFAULT gen_random_uuid(),
    show_all_public_layers boolean default false,
    is_public              boolean default false,
    owner                  text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS apps_layers
(
    app_id       uuid    NOT NULL,
    layer_id     text    NOT NULL,
    is_default   boolean default false,
    display_rule jsonb,
    style_rule   jsonb,
    PRIMARY KEY (app_id, layer_id),
    CONSTRAINT fk_apps_layers_apps
        FOREIGN KEY (app_id)
            REFERENCES apps (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_apps_layers_layers
        FOREIGN KEY (layer_id)
            REFERENCES layers (public_id)
            ON DELETE CASCADE
);

ALTER TABLE layers DROP COLUMN IF EXISTS display_rule;

insert into apps (id, show_all_public_layers, is_public, owner)
values ('58851b50-9574-4aec-a3a6-425fa18dcb54', true, true, 'layers_db')
ON CONFLICT (id) DO NOTHING;