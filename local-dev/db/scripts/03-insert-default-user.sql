--liquibase formatted sql

--changeset desafio:insert-default-user
INSERT INTO users (username, password, enabled) VALUES ('user', '$2a$10$FleDDx5lUiMnaDqaHOz1eeO.Kpxh5pAQOvlAzjAP6eTRjMED3jniS', true) ON CONFLICT DO NOTHING; --user/password
--rollback delete from users where username='user';

--changeset desafio:insert-a-role
INSERT INTO authorities (username, authority) VALUES ('user', 'ROLE_USER');
--rollback delete from authorities where username='user';
