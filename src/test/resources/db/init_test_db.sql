create extension if not exists "uuid-ossp";
create extension if not exists "btree_gin";

CREATE TABLE layers_group_properties
(
    id                 integer generated always as identity NOT NULL,
    name               text UNIQUE                          NOT NULL,
    is_opened          boolean,
    mutually_exclusive boolean,
    "order"            integer,
    PRIMARY KEY (id)
);

CREATE TABLE layers_category_properties
(
    id                 integer generated always as identity NOT NULL,
    name               text UNIQUE                          NOT NULL,
    is_opened          boolean,
    mutually_exclusive boolean,
    "order"            integer,
    PRIMARY KEY (id)
);

CREATE TABLE layers
(
    id                    integer generated always as identity,
    public_id             text UNIQUE              NOT NULL,
    name                  text,
    url                   text,
    type                  text,
    description           text,
    copyrights            text,
    last_updated          timestamp with time zone not null default CURRENT_TIMESTAMP,
    source_updated        timestamp with time zone,
    access_time           timestamp with time zone,
    owner                 text,
    is_public             boolean,
    category_id           integer                  REFERENCES layers_category_properties (id) ON DELETE SET NULL,
    group_id              integer                  REFERENCES layers_group_properties (id) ON DELETE SET NULL,
    properties            jsonb,
    is_visible            boolean,
    is_dirty              boolean,
    zoom_visibility_rules jsonb,
    geom                  geometry,
    feature_properties    jsonb,
    PRIMARY KEY (id)
);

CREATE TABLE layers_features
(
    feature_id   text                     not null,
    layer_id     integer                  not null REFERENCES layers (id) ON DELETE CASCADE,
    properties   jsonb,
    geom         geometry,
    last_updated timestamp with time zone not null default CURRENT_TIMESTAMP,
    zoom         integer                           default 999,
    UNIQUE (feature_id, layer_id, zoom)
);

CREATE TABLE layers_dependencies
(
    layer_id            integer,
    parent_id           integer,
    recalculation_rules jsonb,
    display_child       boolean,
    to_calculate        boolean,
    CONSTRAINT fk_layers_dependencies_layer_id
        FOREIGN KEY (layer_id)
            REFERENCES layers (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_layers_dependencies_parent_id
        FOREIGN KEY (parent_id)
            REFERENCES layers (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS apps
(
    id                     uuid NOT NULL DEFAULT gen_random_uuid(),
    show_all_public_layers boolean,
    is_public              boolean,
    owner                  text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS apps_layers
(
    app_id       uuid    NOT NULL,
    layer_id     text    NOT NULL,
    is_default   boolean,
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

