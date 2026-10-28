
- **Backend:** API REST con autenticacion JWT, Spring Security y JPA/Hibernate.
- **Frontend:** SPA en Angular que consume la API.
- **Base de datos:** PostgreSQL 15 corriendo en Docker (puerto 5433).

## Requisitos previos

- [Docker](https://www.docker.com/) y Docker Compose
- Java 17+
- Node.js 18+ y npm


### 1. Levantar la base de datos

```bash
cd prestamos
docker compose up -d
```

Esto inicia un contenedor PostgreSQL con la siguiente configuracion:

| Parametro | Valor            |
|-----------|------------------|
| Host      | localhost        |
| Puerto    | 5433             |
| Base      | prestamosdb      |
| Usuario   | postgres         |
| Password  | prestamos123     |

### 2. Iniciar el backend

```bash
cd prestamos
./mvnw spring-boot:run o mvn spring-boot:run
```

El servidor arranca en `http://localhost:8080`. Al iniciar por primera vez, se crean automaticamente los roles y dos usuarios de prueba:

| Email              | Password | Rol        |
|--------------------|----------|------------|
| usuario@test.com   | 123      | ROLE_USER  |
| admin@test.com     | 123      | ROLE_ADMIN |

### 3. Iniciar el frontend

```bash
cd frontend
npm install
npm start
```

La aplicacion estara disponible en `http://localhost:4200`.

## Endpoints principales

| Metodo | Endpoint                  | Acceso        |
|--------|---------------------------|---------------|
| POST   | /api/auth/login           | Publico       |
| POST   | /api/auth/register        | Publico       |
| GET    | /api/loans/**             | Autenticado   |
| GET    | /api/loans/all            | Solo ADMIN    |
| PUT    | /api/loans/*/status       | Solo ADMIN    |
