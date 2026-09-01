# Product API

A small REST API for managing products and their item quantities. It uses Spring Boot, JPA/Hibernate, PostgreSQL, JWT authentication, and Docker.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer
- Docker and Docker Compose (optional, for the database)

The application code is in `product/product`.

Check the selected Java before using Maven:

```bash
java -version
```

The version must be 17 or newer. Docker builds use Java 17 inside the image, so a Java 17 installation is not needed on the host for the Compose workflow.

## Run with PostgreSQL

1. Start PostgreSQL:

   ```bash
   cd product/product
   docker compose up -d postgres
   ```

2. Start the application:

   ```bash
   mvn spring-boot:run
   ```

The API runs on `http://localhost:8080` when HTTPS enforcement is disabled for local development:

```bash
REQUIRE_HTTPS=false mvn spring-boot:run
```

For production, keep `REQUIRE_HTTPS=true` and terminate TLS at the application or at a trusted reverse proxy. The default database credentials are `products/products`.

## Run with Docker Compose

Build and start both services. The Dockerfile compiles the application inside a Java 17 build image:

```bash
cd product/product
docker compose up --build
```

The local Compose setup uses HTTP on port 8080. Use `REQUIRE_HTTPS=true` only when TLS is configured by the application or a reverse proxy.

The PostgreSQL data is stored in the `product-db` Docker volume.

## Authentication

Register and log in to receive an access token and refresh token:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"password123"}'

curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"password123"}'
```

Send the access token on protected requests:

```text
Authorization: Bearer <access-token>
```

Refresh tokens are single-use. Calling `/api/v1/auth/refresh` revokes the old token and returns a new token pair. Product deletion requires the `ADMIN` role.

## API endpoints

All product endpoints use JSON and require authentication unless stated otherwise.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/auth/register` | Create a user |
| POST | `/api/v1/auth/login` | Get access and refresh tokens |
| POST | `/api/v1/auth/refresh` | Rotate a refresh token |
| GET | `/api/v1/products` | List products with pagination |
| GET | `/api/v1/products/{id}` | Get one product |
| GET | `/api/v1/products/{id}/items` | Get a product's items |
| POST | `/api/v1/products` | Create a product |
| PUT | `/api/v1/products/{id}` | Replace a product and its items |
| DELETE | `/api/v1/products/{id}` | Delete a product; admin only |

Create or update example:

```json
{
  "productName": "Widget",
  "items": [
    { "quantity": 10 },
    { "quantity": 4 }
  ]
}
```

Pagination uses `page` and `size`, for example `/api/v1/products?page=0&size=20`. Invalid input returns a JSON error containing the invalid fields.

## Configuration

These environment variables can override the defaults:

| Variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/products` | Database URL |
| `DB_USERNAME` | `products` | Database user |
| `DB_PASSWORD` | `products` | Database password |
| `JWT_SECRET` | Development value | Secret used to sign JWTs; replace it in production |
| `REQUIRE_HTTPS` | `true` | Redirect HTTP requests to HTTPS |
| `APP_CORS_ORIGIN` | `http://localhost:3000` | Allowed browser origin |

Swagger UI is available at `/swagger-ui.html`, and the OpenAPI document is at `/api-docs`.

## Tests

Tests use JUnit 5, Mockito, Spring Boot Test, and an H2 in-memory database:

```bash
cd product/product
mvn clean test
```