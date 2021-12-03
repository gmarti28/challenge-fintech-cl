--liquibase formatted sql

--changeset desafio:create-authorities-table
DROP TABLE IF EXISTS authorities;

CREATE TABLE authorities
(
username  VARCHAR(50) NOT NULL,
authority VARCHAR(50) NOT NULL,
FOREIGN KEY (username) REFERENCES users (username)
);
--rollback DROP TABLE authorities;

--changeset desafio:create-authorities-index
CREATE UNIQUE INDEX idx_auth_username
  on authorities (username,authority);
--rollback DROP INDEX idx_auth_username;

