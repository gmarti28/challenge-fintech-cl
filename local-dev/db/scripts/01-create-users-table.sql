--liquibase formatted sql

--changeset desafio:create-users-table
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    username VARCHAR(50)  PRIMARY KEY,
    password VARCHAR(200) NOT NULL,
    enabled  BOOLEAN NOT NULL DEFAULT true
);
--rollback DROP TABLE users;