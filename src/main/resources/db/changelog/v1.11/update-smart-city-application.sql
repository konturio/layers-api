--changeset layers-api-migrations:v1.11/update-smart-city-application.sql runOnChange:true

update apps
    set show_all_public_layers = false
where id = '634f23f5-f898-4098-a8bd-09eb7c1e1ae5';
