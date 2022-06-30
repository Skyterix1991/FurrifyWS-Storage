![furrify title](https://user-images.githubusercontent.com/33985207/133672173-2d7ff06e-6f94-4742-a201-c54a85c5839a.png)

Microservices infrastructure for content management.

Documentation: - Not released yet REST endpoints (in
development): https://documenter.getpostman.com/view/9259933/TzRUBnQm

## Docker

This project is now docker supported. You can build it using either `build.sh` or `build.bat`.

After that you need to edit `docker-compose.yml` for your needs. All relevant environment variables are commented in
that file.

When you are ready to go, just do `docker-compose up -d` to start all the microservices.

Requirements for build:

- Maven
- Docker
- Docker-compose

External requirements:

- Keycloak
- Kafka
- Confluent schema registry
- Infinispan Cache Server

## Setup Keycloak

To set up keycloak you can use [this](#) docker image. In panel, you can import settings from [this](#) file.
! Links are not yet available as project version 0.0.1 is not released.

## Setup Infinispan

You will need to set up following data containers in server management console:

- "sources"

## Databases

You can configure database you want to use with env variables listed in `docker-compose.yml` file.

Supported:

- MySQL (Tested)
- PostgreSQL (Not tested)

## Compile project yourself

If you wish to compile the project yourself don't forget to set all env variables listed in `docker-compose.yml`.

To build the project follow docker section above.

## Tests

Whole project is covered by tests using JUnit 5. Note that running tests requires dev profile or else there will be
thrown exceptions as default profile is production.

To run those you can use Maven command: `mvn clean compile test -Dspring.profiles.active=dev`
