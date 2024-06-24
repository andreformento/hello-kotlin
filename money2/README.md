# Application

- Java 17
- Gradle
- Kotlin
- R2DBC (Postgres)
- Spring
- WebFlux
- coroutines

## Commands

- Run app
  ```shell
  make run
  ```

- Test
  ```shell
  make test
  ```

## API

- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/health/liveness
- http://localhost:8080/actuator/health/readiness
- http://localhost:8080/actuator/prometheus

| URI pattern                                          | Method   | Description                  |
|------------------------------------------------------|----------|------------------------------|
| `/organizations`                                     | `GET`    | Get all organizations        |
| `/organizations`                                     | `POST`   | Create a organization        |
| `/organizations/{organization-id}`                   | `GET`    | Get one organization         |
| `/organizations/{organization-id}`                   | `PUT`    | Update a organization        |
| `/organizations/{organization-id}`                   | `DELETE` | Delete a organization        |
| `/organizations/{organization-id}/shares`            | `GET`    | Shares from a organization   |
| `/organizations/{organization-id}/shares/{share-id}` | `GET`    | A Shares from a organization |

### Terminal example

Run make and be happy

- `make signup`
- `make login`
- `make get-user`

- create an organization
```shell
curl -v -b cookies.txt -X POST 'http://localhost:8080/organizations' \
  -H 'Content-Type: application/json' \
  --data-raw '{"name":"a new organization", "description":"My first organization"}'
```

Response header `Location` contains the new ID

## References
- https://spring.io/blog/2019/04/12/going-reactive-with-spring-coroutines-and-kotlin-flow
- https://github.com/hantsy/spring-kotlin-coroutines-sample/blob/master/data-r2dbc-fn/src/main/kotlin/com/example/demo/DemoApplication.kt
- https://www.tolkiana.com/introduction-to-spring-data-r2dbc-with-kotlin/
- https://github.com/hantsy/spring-kotlin-coroutines-sample
- https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html
- https://hantsy.medium.com/reactive-accessing-rdbms-with-spring-data-r2dbc-d6e453f2837e
- https://docs.spring.io/spring-framework/docs/current/reference/html/languages.html#controllers
- https://www.baeldung.com/micrometer
- https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#actuator.metrics.export.prometheus
- https://spring.io/blog/2017/08/01/spring-framework-5-kotlin-apis-the-functional-way

## TODO
- sec 
  - [Springboot + Kotlin](https://medium.com/@jonssantana/authentication-e-authorization-usando-springboot-kotlin-382681024d08)
  - [reactive](https://medium.com/@jaidenashmore/jwt-authentication-in-spring-boot-webflux-6880c96247c7)
  - [private public key](https://www.novixys.com/blog/how-to-generate-rsa-keys-java/)
  - google integration
- log
- test
  - unit test junit5 + [Mockk](https://mockk.io/)
  - integration test
  - testcontainer
- docker
  - https://blog.jetbrains.com/idea/2021/01/run-targets-run-and-debug-your-app-in-the-desired-environment/
  - docker compose with graphite
- hex? entity, request, response, model
- kubernetes (Helm) deployments
- Spring Boot DevTools
- code formater [spotless](https://github.com/diffplug/spotless)
- [https://github.com/JetBrains/Exposed](Exposed?)
- opentracing/jaeger
- rest api integration with other application
- [flyway](https://flywaydb.org)
