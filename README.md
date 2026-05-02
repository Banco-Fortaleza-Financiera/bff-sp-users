# bff-ux-accounts

Proyecto Java 21 con Gradle Groovy, Spring Boot 3.x, Spring Cloud OpenFeign, OpenAPI Generator, pruebas con JUnit 5 y JaCoCo.

## Requisitos

- Java 21
- Gradle Wrapper incluido en el proyecto
- Git para instalar el hook de pre-commit

## Como ejecutar

```sh
./gradlew bootRun
```

La aplicacion inicia en `http://localhost:8080`.

## Como correr pruebas

```sh
./gradlew test
```

Para ejecutar el build completo:

```sh
./gradlew clean build
```

## Cobertura JaCoCo

Generar reporte:

```sh
./gradlew jacocoTestReport
```

Ver el reporte HTML en:

```text
build/reports/jacoco/test/html/index.html
```

Validar cobertura minima del 80%:

```sh
./gradlew jacocoTestCoverageVerification
```

## Regenerar OpenAPI

La especificacion vive en:

```text
src/main/resources/openapi/specification.yaml
```

Regenerar codigo:

```sh
./gradlew openApiGenerate
```

El codigo generado queda en `build/generated/openapi` y se agrega al `sourceSets.main`. No se mezcla con el codigo manual.

## Instalar pre-commit

```sh
./gradlew installGitHooks
```

El hook ejecuta:

```sh
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

## Arquitectura

La aplicacion usa una arquitectura hexagonal simplificada:

- `domain`: modelos y excepciones de dominio sin dependencias de Spring.
- `services`: casos de uso y logica de negocio.
- `repository`: puertos e implementaciones externas.
- `handler`: controladores REST, DTOs y manejo de errores.
- `feign`: clientes externos con Spring Cloud OpenFeign.
- `configuration`: configuracion Spring.
- `utils`: mappers y utilidades.

El flujo de ejemplo crea cuentas bancarias: el `handler` recibe el request, el `service` valida reglas de negocio, el `repository` persiste en memoria y el adaptador Feign consulta un perfil externo.

## Docker

Construir imagen:

```sh
docker build -t bff-ux-accounts .
```

Ejecutar contenedor:

```sh
docker run --rm -p 8080:8080 bff-ux-accounts
```
