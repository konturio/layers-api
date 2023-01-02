--changeset layers-api-migrations:v1.12/create-oam-application-13984.sql runOnChange:false

insert into apps (id, show_all_public_layers, is_public, owner)
values ('1dc6fe68-8802-4672-868d-7f17943bf1c8', false, true, 'layers_db')
ON CONFLICT (id) DO NOTHING;