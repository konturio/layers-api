--changeset layers-api-migrations:v1.9/create-smart-city-application-12813.sql runOnChange:false

insert into apps (id, show_all_public_layers, is_public, owner)
values ('634f23f5-f898-4098-a8bd-09eb7c1e1ae5', true, true, 'layers_db')
ON CONFLICT (id) DO NOTHING;