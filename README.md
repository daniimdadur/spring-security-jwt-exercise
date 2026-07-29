# Spring Security JWT Exercise

A Spring Boot REST API with JWT-based authentication and authorization using Spring Security, featuring role-based access control with fine-grained permissions.

## Features

- **JWT Authentication** — Access token (15 min) + refresh token (24 hours, HTTP-only cookie)
- **Role-Based Access Control** — `USER`, `ADMIN`, `SUPER_ADMIN` with role hierarchy
- **Fine-Grained Permissions** — 13 granular permissions (CRUD per resource, etc.)
- **Refresh Token Rotation** — Revoke all devices on re-login, logout single or all devices
- **Rate Limiting** — 30 requests/min sliding window on auth endpoints
- **Pagination** — Pageable support on all list endpoints
- **Audit Trail** — Created/Updated/Deleted timestamps and actors
- **Soft Delete** — Data preserved with `deleted_at` timestamp
- **Scheduled Cleanup** — Expired refresh tokens cleaned daily at 3 AM

## Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 4.1.0 |
| Language | Java 21 |
| Security | Spring Security 6.x |
| JWT | JJWT 0.13.0 (HMAC-SHA256) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL (Docker Compose) |
| Build | Maven |
| Validation | Jakarta Bean Validation |
| Code Gen | Lombok |

## Prerequisites

- Java 21
- Docker & Docker Compose (for MySQL)
- Maven (or use `mvnw` wrapper)

## Getting Started

### 1. Clone and configure

```bash
git clone <repo-url>
cd spring-security-jwt-exercise
```

### 2. Configure environment variables (optional, defaults exist)

```bash
set JWT_ACCESS_SECRET=your-base64-256bit-secret
set JWT_REFRESH_SECRET=your-base64-256bit-secret
set COOKIE_SECURE=false   # set true for HTTPS
```

Generate a secret key:
```bash
# run JwtKeyGenerator main class to generate HMAC-SHA256 keys
```

### 3. Start MySQL

```bash
docker compose up -d
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The app starts at `http://localhost:8080`.

## Default Users

| Email | Password | Role |
|-------|----------|------|
| superadmin@guvaren.com | superadmin | SUPER_ADMIN |
| admin@guvaren.com | admin | ADMIN |
| user@guvaren.com | user | USER |

Seed data is loaded automatically on startup via `DataInitializer`.

## API Overview

### Authentication (public) — `/api/v1/auth`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login (multi-device) |
| POST | `/login-logout` | Login & revoke other devices |
| POST | `/refresh-token` | Refresh access token |
| POST | `/logout` | Logout this device |
| POST | `/logout-all-devices` | Logout all devices |

### Users — `/api/v1/users`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/` | `ROLE_USER` | List users |
| POST | `/{userId}/roles` | `ROLE_SUPER_ADMIN` | Assign roles |

### Fakultas — `/api/v1/fakultas`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/` | `fakultas:read` | List fakultas |
| GET | `/{id}` | `fakultas:read` | Get by ID |
| POST | `/` | `fakultas:create` | Create |
| PUT | `/{id}` | `fakultas:update` | Update |
| DELETE | `/{id}` | `fakultas:delete` | Delete |

## Project Structure

```
com.guvaren.securityjwt
├── SpringSecurityJwtExerciseApp
├── base/               # Audit, Response wrapper, DataInitializer
├── config/             # Spring configuration
├── enums/              # App-level enums
├── exception/          # Custom exceptions + GlobalExceptionHandler
├── master/auth/        # Auth & authorization module
│   ├── controller/     # REST controllers
│   ├── dto/            # Request/Response DTOs
│   ├── entity/         # JPA entities
│   ├── enums/          # Roles, Permissions, TokenType
│   ├── repository/     # Spring Data JPA repos
│   ├── security/       # JWT filter, SecurityConfig, handlers
│   └── service/        # Business logic
├── master/fakultas/    # Sample CRUD module
└── util/               # UUID, Cookie, JWT Key Gen utilities
```

## Security

- **Stateless** — No HTTP sessions, CSRF disabled
- **JWT Filter** — `Bearer` token extracted, validated, `SecurityContext` set on every request
- **Token Storage** — Refresh tokens hashed (SHA-256) in database
- **Cookie** — HTTP-only, SameSite=Strict
- **Password** — BCrypt encoded
- **Role Hierarchy** — `SUPER_ADMIN > ADMIN > USER` (inherits permissions)

## Build and Test

```bash
./mvnw clean test
```
