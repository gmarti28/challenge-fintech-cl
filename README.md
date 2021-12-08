# challenge-fintech-cl

## Prerequisitos

* [Docker Desktop](https://www.docker.com/products/docker-desktop) y `docker-compose` (se incluye con Docker Desktop) instalados y disponibles
* JDK 11 (Ej: AdoptOpenJDK 11). Se puede instalar en linux y mac con [SdkMan](https://sdkman.io/)
  * Se recomienda instalar un SDK Hotspot aunque para este challenge la cantidad de ejecuciones de los métodos no hará 
diferencia en la performance debido a que los JDK _hotspot_ compilan el _bytecode_ a nativo luego de 10000 ejecuciones del mismo método.
  * Para instalar con SdkMan `sdk install java 11.0.11.hs-adpt`
* Opcional: [Maven](https://maven.apache.org/). (El proyecto incluye Maven Wrapper) 

## Ejecución con Docker

#### Encendido
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

#### Apagado del servicio y su dependencia

```
local-dev/compose-down.sh
```

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

Ejecución desde un IDE 
---
La configuración de JPA se encuentra en el archivo `src/main/resources/application.yml` y es importante entender que 
la URL de Postgres suministrada sólo funciona iniciando la aplicación con docker-compose: 

```
  datasource:
    url: jdbc:postgresql://postgres:5432/desafio
```

Nótese que se suministra como hostname "postgres" el cual no es un hostname real (resoluble por dns) sino el nombre
del container de Postgres resoluble gracias al mecanismo de networking de Docker Compose. Véase [Compose networking](https://docs.docker.com/compose/networking/)
para mas información. 

Para ejecutar o depurar la aplicación desde el IDE se necesita:
1. Editar el **_hostname_** de _postgres_ en `application.yml` (por ejemplo `localhost:5432`)
2. **Comentar** con `#` las líneas en donde se define el servicio "`application:`" en `local-dev/docker-compose.yml`
3. Iniciar la base de datos con local-dev/compose-up.sh  
4. Ejecutar en el IDE el método main de la clase `src/main/java/com/gastonmartin/desafio/DesafioApplication.java` 


## Consideraciones de la implementación 

CSRF - Cross Site Request Forgery 
---
En sistemas productivos donde el backend se utiliza desde otra aplicación (una UI) resulta crucial tener un mecanismo de
protección contra los ataques de CSRF/XSRF en donde un script malicioso alojado en un sitio malicioso redirecciona al 
navegador de la víctima a un recurso de nuestro backend sin que la víctima lo detecte, personificándola a través de las
cookies guardadas en el navegador. El mecanismo estándar de protección consiste en generar un TOKEN del lado del backend
asociado a la sesión del usuario, configurarlo como una cookie httpOnly (no legible desde ningún script en el cliente)
que debe ser enviado junto con los request subsiguientes en forma de Cookie o Header para que el backend acepte los requests. 
En Spring Security se implementa muy facilmente, pero dificulta las pruebas desde Swagger (no asi con Postman) 
---
En la consigna del challenge no se pide la adopción de medidas de protección contra CSRF, pero de ser necesarias puedo
agregarlas si me lo indican. 

CORS - Cross Origin Resource Sharing
---
Debido a que no se implementa una UI para este backend, y se desconoce el Origin del cual provendrían los requests, no
se implementó ninguna protección CORS. De hacerlo hubiese tenido que indicar un Access-Control-Allow-Origin permisivo (*)

JWT - Json Web Token
---
En entornos productivos donde se requiere facilitar la escalabilidad horizontal, y para evitar mantener y sincronizar la sesión
entre multiples instancias del mismo servicio, se recomienda implementar JWT Token.

Cuando se usa JWT el backend en vez de guardar y sincronizar la información de la sesión la describe en una estructura JSON. 
Esta estructura puede contener información de autenticación y de autorización, como ser username, roles, autorizaciones, 
tiempo de expiración, etc.) y esta información se encodea con base64 para simplificar el intercambio. 
Luego el backend firma criptográficamente el header y body del JSON con un secret conocido únicamente por el backend, 
lo cual evita que cualquier cliente pueda alterar (_tampering_) los _claims_ incluidos en el JSON (por ejemplo impersonando 
a otro usuario diferente, alterando los roles, etc.) dado que de hacerlo la firma no coincidiría y el backend lo detectaria. 
La adopción de un JWT Token puede volver innecesaria la adopción de un token CSRF bajo ciertas precauciones (tiempos de 
expiración de la sesión razonablemente cortos)


Configuracion de Spring Security
---

La seguridad se maneja con **Spring Security**, usando **Bcrypt** como `PasswordEncoder` y la implementación `JdbcUserDetailsManager` 
de `UserDetailsService` delegada en la base de datos _Postgres_. Esta configuración se puede ver en el archivo 
`src/main/java/com/gastonmartin/desafio/config/ProjectSecurityConfig.java`

Dado que el mecanismo `withDefaultSchema()` de `AuthenticationManagerBuilder` genera DDL inválido para Postgres, se 
utiliza _Liquibase_ para inicializar las tablas de `USERS` y `AUTHORITIES` requeridas por _Spring Security_.

Para **facilitar** el **testing inicial** de la aplicación se pre generan credenciales (`user/password`) para un usuario mediante _Liquibase_. 

Se definió un solo **rol de seguridad** _"ROLE_USER"_ porque no se tiene mayor cantidad de endpoints que haga necesaria una separación más fina. 

Se restringió el **acceso a algunos recursos** usando `MvcMatcher` por considerarse mas seguro que `AntMatcher`. Se definió que todo recurso no enlistado
en la configuración **no sea accesible**. Algunos recursos se pueden acceder sin estar autenticado (por razones lógicas)

Debido al requerimiento de mantener un **registro de todos los endpoints accedidos** se implementó un `AuditRequestsFilter` que guarda 
los **accesos a los endpoints** en la base de datos. El modelo de datos para este mecanismo se configura automáticamente con _Hibernate_.
Se podría haber delegado enteramente en los scripts de _liquibase_ desactivando la funcionalidad de actualización automática de _Hibernate_. 

Esta funcionalidad se implementó usando _JPA Repository_ con **métodos autogenerados** por la interfaz `AuditRepository`. 
Me apalanqué en la abstracción `Pageable` de Spring Data para **devolver el resultado paginado**. Podría haber implementado una lógica 
más liviana pidiendo parámetros offset y limit, pero me pareció una reinvención innecesaria. 

Asimismo la implementación de la auditoría mediante un filtro de Spring agregado a la `Filter Chain` se podría haber reemplazado 
por un _bean autowired_ al servicio o al repositorio de `AuditRepository` que actualice la tabla al ingresar o salir del _controller_ (la lógica 
no se especificó en la consigna). La implementación en un filtro me pareció más concisa y elegante.

Debido a que los filtros no tienen acceso al _Autowiring_ de Spring, tuve que implementar una lógica custom de acceso al contexto de Spring dentro del filtro. 
Además el filtro se implementó como `OncePerRequestFilter` para garantizar que no se ejecute más de una vez por request. 

No se configura la auditoría para todas las rutas ya que no me interesa auditar por ejemplo los accesos a Swagger. 

Sobre la implementacion de Form Login
---
Spring Security provee una implementación _default_ del **formulario de login**, para lo cual agrega un endpoint oculto 
(reconfigurable) de `/login` que **solo acepta payload** de tipo **x-www-form-encoded**. 
Mediante la property `springdoc.show-login-endpoint = true` se puede **incluir a dicho endpoint en swagger**, pero encontré 
un BUG: la definición no coincide con lo que el endpoint acepta. Swagger propone un payload de tipo JSON pero el endpoint 
solo acepta x-www-form-encoded por lo que **no funciona desde swagger**. 
(La configuración de Swagger se mantiene en un archivo `open-api.properties` separado de las properties de la aplicación.) 

Por ese motivo, implementé un endpoint de `/login` propio, que me permite manejar mejor el comportamiento y la documentación. 
Otro problema del `/login` default implementado por Spring que se comparte con el de /logout es que no respeta los filters 
y me impedía registrar los logins y los logout. Es por ello que implemente una version propia de ambos dos. 

Se ha configurado el botón "`Authorize`" en swagger que permite especificar **credenciales** para todos los endpoints que necesitan login. 
Swagger traduce esa información en _http basic authentication_ incluyendo un _header_ `Authorization` cuando se suministran credenciales.
Si se intenta llamar a un endpoint segurizado desde swagger sin cargar las credenciales primero con dicho botón, 
aparecerá un popup pidiendo usuario y contraseña. 

