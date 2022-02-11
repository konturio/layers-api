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
    display_rule          jsonb,
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

CREATE TABLE layers_style
(
    id         integer generated always as identity,
    layer_id   integer,
    style_rule jsonb,
    CONSTRAINT fk_layers_style
        FOREIGN KEY (layer_id)
            REFERENCES layers (id)
            ON DELETE CASCADE
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

