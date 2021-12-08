# challenge-fintech-cl

## Prerequisitos

* [Docker Desktop](https://www.docker.com/products/docker-desktop) y `docker-compose` (se incluye con Docker Desktop) instalados y disponibles
* JDK 11 (Ej: AdoptOpenJDK 11). Se puede instalar en linux y mac con [SdkMan](https://sdkman.io/)
  * Se recomienda instalar un SDK Hotspot aunque para este challenge la cantidad de ejecuciones de los métodos no hará 
diferencia en la performance debido a que los JDK _hotspot_ compilan el _bytecode_ a nativo luego de 10000 ejecuciones del mismo método.
  * Para instalar con SdkMan `sdk install java 11.0.11.hs-adpt`
* Opcional: [Maven](https://maven.apache.org/). (El proyecto incluye Maven Wrapper) 

## Ejecución con Docker

```
local-dev/compose-up.sh
```

Esa sentencia utiliza _docker-compose_ (ver `local-dev/docker-compose.yml`) para: 

* Iniciar un contenedor de _Postgres_ a partir de una imagen pública que se _customiza_ con un _script_ incluido en la ejecución
* Inicializar el esquema "desafio" de la base utilizando _liquibase_. Este esquema se usa para _Spring Security_ dado que la 
autogeneración del modelo en el framework no es compatible con el dialecto de _Postgres_ (funciona sólo con _H2_)
* El contenedor de _liquibase_ cumple su función y finaliza. 
* **Compilar** la **aplicación** en un _layer_ **intermedio** de docker, utilizando `mvnw` (Maven Wrapper) 
* Iniciar un contenedor que escucha en el puerto 8080 a partir de la imagen de la aplicación compilada en el paso anterior. 

El enfoque usado para construir la aplicación y sus dependencias con `docker-compose` no son **_production-ready_** ya que 
se desconocen los mecanismos de CI/CD y los lineamientos para definir variables de entorno y secrets. Por ejemplo [sops](https://github.com/mozilla/sops), [Helm](https://helm.sh/docs/chart_template_guide/values_files/), etc. 

## Acceso a Swagger UI

* Una vez iniciada la base y la aplicación _dockerizada_ dirigir el navegador a [http://localhost:8080](http://localhost:8080)

## Endpoints disponibles

| Metodo | URL       | Segurizado | Uso                                          |
|--------|-----------|:----------:|----------------------------------------------|
| POST   | /signup   | NO         | Registro de nuevos usuarios                  |
| POST   | /login    | NO         | _Login_ de usuarios con usuario y contraseña |
| POST   | /math/add | SI         | Servicio para sumar dos números              |
| GET    | /audit    | SI         | Consulta paginada de acceso a los endpoints  |
| POST   | /logout   | SI         | _Desloggeo_ de usuarios                      |

Además existe un endpoint oculto (autogenerado por Spring Security) `/api/logout` que no genera entradas en la base de 
auditoría. Esto se debe a que Spring lo implementa sin respetar los ejecución de la _filter chain_

Al usar el endpoint de `/login` el server configura una cookie **JSESSIONID** _httpOnly_ que se puede enviar en los requests
posteriores para mantener la sesión del usuario. El endpoint de `/logout` invalida la sesión. 

Para todos los requests que requieren estar autenticado se puede incluir un header de Authorization con formato username:password o su base64

## Pruebas por Postman

* Se pueden ejercitar los distintos _endpoints_ de la aplicación importando la _collection_ ```postman/Tenpo.postman_collection.json```
* Esta _collection_ contiene Test Scripts que setean variables de entorno. Siguiendo buenas prácticas no se guardan como 
variables globales sino que se establecen como variables de entorno. 
* Por tal motivo la _collection_ necesita ejecutarse con un _Environment_ (se puede importar el environment vacío desde la 
carpeta `postman/Local.postman_environment.json`)
* Esta collection dá de alta usuarios con _usernames_ generados al azar y contraseña '**password**'
* Además se provee un primer usuario pre-inicializado desde _liquibase_ ("`user/password`")

## Ejecución desde un IDE 

## Consideraciones de la implementacion 

CRSF - Cross Site Request Forgery 
---

### Iniciar base de datos
# Seguridad

La seguridad se maneja con Spring Security, usando Bcrypt como PasswordEncoder y la implementacion JdbcUserDetailsManager 
de UserDetailsService delegada en la base de datos Postgres

Dado que el mecanismo withDefaultSchema() de AuthenticationManagerBuilder genera DDL invalido para Postgres, se 
utiliza Liquibase para inicializar las tablas de USERS y AUTHORITIES requeridas por Spring Security.

Para facilitar el testing inicial de la aplicación se generan credenciales (user/password) mediante Liquibase. 



