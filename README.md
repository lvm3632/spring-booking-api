# API para sistema de reservaciones para hoteles, vuelos y tours

API desarrollada en Spring Boot para gestionar reservaciones de viajes (hoteles, vuelos, tours) con varias funcionalidades de Spring.

## Funcionalidades

- **Spring Data JPA**: Persistencia en base de datos relacional.
- **Spring Web**: Endpoints HTTP para reservas.
- **Manejo de Errores**: Estrategias de manejo de excepciones.
- **Swagger**: Documentación interactiva de la API.
- **Spring Web Client**: Consumo de servicios externos.
- **Email y Notificaciones**: Envío de emails.
- **Cache con Redis**: Optimización de respuestas.
- **Autenticación JWT**: Seguridad con OAuth2 y JWT.
- **AOP**: Programación orientada a aspectos.
- **RabbitMQ**: Mensajería asincrónica.
- **Docker Compose**: Uso de docker compose para levantar los servicios como Postgres, Redis, Mongo y RabbitMQ en un entorno local de bridge.
- **Postman**: Uso de Postman para hacer prueba a los endpoints, con uso de la consola y scripts en JavaScript.

## Estructura del Proyecto

```plaintext
├── api
│   ├── controllers
│   ├── error_handler
├── aspects
├── config
├── domain
│   ├── entities
│   ├── repositories
├── infrastructure
│   ├── abstract_services
│   ├── dtos
│   ├── helpers
│   ├── services
├── util
│   ├── annotations
│   ├── constants
│   ├── enums
│   ├── exceptions
```

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/usuario/best-travel-api.git
2. Configura application.properties.


3. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run

## Documentación
Accede a la documentación en Swagger: http://localhost:8080/swagger-ui.html

