# challenge-fintech-cl

# Inicializacion 

## Prerequisitos

* Docker y docker-compose instalados y disponibles
* JDK 11 (Ej: AdoptOpenJDK 11)
* Maven 

# Inicializacion 

```
cd local-dev
./compose-up.sh
```

# Seguridad

La seguridad se maneja con Spring Security, usando Bcrypt como PasswordEncoder y la implementacion JdbcUserDetailsManager 
de UserDetailsService delegada en la base de datos Postgres

Dado que el mecanismo withDefaultSchema() de AuthenticationManagerBuilder genera DDL invalido para Postgres, se 
utiliza Liquibase para inicializar las tablas de USERS y AUTHORITIES requeridas por Spring Security.

Para facilitar el testing inicial de la aplicación se generan credenciales (user/password) mediante Liquibase. 



