# Technical Requirements Document (TRD)

## Spring Security JWT Exercise

---

## 1. Dokumentasi Proyek

| Attribute             | Value                                              |
|-----------------------|----------------------------------------------------|
| **Project Name**      | spring-security-jwt-exercise                        |
| **Group ID**          | com.guvaren                                        |
| **Artifact ID**       | spring-security-jwt-exercise                        |
| **Version**           | 0.0.1-SNAPSHOT                                     |
| **Base Package**      | com.guvaren.securityjwt                            |
| **Framework**         | Spring Boot 4.1.0                                  |
| **Java Version**      | 21                                                 |
| **Build Tool**        | Maven                                              |
| **Database**          | MySQL (via Docker Compose)                         |
| **ORM**               | Spring Data JPA / Hibernate                        |
| **Security Framework**| Spring Security                                    |
| **JWT Library**       | JJWT 0.13.0                                        |

---

## 2. Arsitektur Sistem

### 2.1 High-Level Architecture

Proyek ini menerapkan arsitektur **layered architecture** dengan pola modular. Setiap modul bisnis terdiri dari lapisan: Controller → Service → Repository → Entity, dengan DTO sebagai pemisah antara kontrak API dan model persistensi.

### 2.2 Struktur Package

```
com.guvaren.securityjwt
├── SpringSecurityJwtExerciseApp          -- Entry point + @EnableJpaAuditing
├── base/                                 -- Base classes (audit, response wrapper)
├── config/                               -- Application-level Spring configuration
├── enums/                                -- Application-level enums
├── exception/                            -- Custom exceptions + GlobalExceptionHandler
├── master/                               -- Business modules
│   ├── auth/                             -- Authentication & Authorization module
│   │   ├── controller/                   -- REST controllers
│   │   ├── dto/req & dto/res/            -- Request/Response DTOs
│   │   ├── entity/                       -- JPA entities
│   │   ├── enums/                        -- Roles, Permissions, TokenType
│   │   ├── repository/                   -- Spring Data JPA repositories
│   │   ├── security/                     -- Security filter chain, JWT filter, handlers
│   │   └── service/                      -- Business logic layer
│   └── fakultas/                         -- Sample CRUD domain module
│       ├── controller/
│       ├── model/                        -- Entity, Request, Response
│       ├── repo/
│       └── service/
└── util/                                 -- Utility classes (UUID, Cookie, JWT Key Gen)
```

### 2.3 Diagram Komponen

```
┌─────────────────────────────────────────────────────────────┐
│                     Client (Browser/App)                     │
└──────────────────────────────┬──────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Security Filter    │
                    │  (JWT Filter)       │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
     ┌────────▼────────┐ ┌────▼────┐  ┌────────▼────────┐
     │ ApiAuthController│ │ ApiUser │  │ FakultasController│
     │ /api/v1/auth/**  │ │Ctrl     │  │ /api/v1/fakultas  │
     └────────┬────────┘ └────┬────┘  └────────┬────────┘
              │               │                │
     ┌────────▼────────┐ ┌────▼────┐  ┌────────▼────────┐
     │ AuthServiceImpl │ │UserSvc  │  │ FakultasSvcImpl  │
     └────────┬────────┘ └────┬────┘  └────────┬────────┘
              │               │                │
     ┌────────▼────────┐ ┌────▼────┐  ┌────────▼────────┐
     │ AccessJwtService │ │UserRepo│  │ FakultasRepo     │
     │ RefreshTokenSvc  │ │RoleRepo│  └────────┬────────┘
     └────────┬────────┘ └────┬────┘           │
              │               │                │
     ┌────────▼────────────────────────────────▼────────┐
     │                 MySQL Database                    │
     └──────────────────────────────────────────────────┘
```

---

## 3. Tech Stack

| Layer                    | Technology                                         |
|--------------------------|----------------------------------------------------|
| **Framework**            | Spring Boot 4.1.0                                  |
| **Language**             | Java 21                                            |
| **Security**             | Spring Security 6.x                                |
| **JWT**                  | JJWT 0.13.0 (jjwt-api, jjwt-impl, jjwt-jackson)  |
| **ORM**                  | Spring Data JPA + Hibernate                        |
| **Database**             | MySQL (latest)                                     |
| **Build**                | Maven                                              |
| **Validation**           | Jakarta Bean Validation (spring-starter-validation)|
| **Serialization**        | Jackson 3.x (tools.jackson.databind)               |
| **Code Generation**     | Lombok                                             |
| **Containerization**     | Docker Compose                                     |
| **Password Encoding**    | BCrypt                                             |

---

## 4. Detail Modul

### 4.1 Modul Authentication & Authorization (`master.auth`)

#### 4.1.1 Purpose

Modul ini menangani seluruh proses autentikasi (login/register), otorisasi (role & permission), serta manajemen token JWT (access token & refresh token).

#### 4.1.2 Components

##### Entities

| Entity             | Table          | Description                                    |
|--------------------|----------------|------------------------------------------------|
| `UserEntity`       | `t_user`       | Menyimpan data pengguna (id, nama, email, password) |
| `RoleEntity`       | `t_roles`      | Menyimpan role (USER, ADMIN, SUPER_ADMIN)      |
| `PermissionEntity` | `t_permissions`| Menyimpan permission fine-grained              |
| `RefreshTokenEntity`| `t_token`     | Menyimpan refresh token (hashed) di database   |

##### Relationships

```
UserEntity ──M:N──> RoleEntity ──M:N──> PermissionEntity
UserEntity ──1:N──> RefreshTokenEntity
```

##### DTOs

| DTO                      | Direction | Description                                    |
|--------------------------|-----------|------------------------------------------------|
| `AuthenticationReq`      | Request   | Login request (email + password)               |
| `RegistrationReq`        | Request   | Registration request (firstName, lastName, email, password) |
| `RolesReq`               | Request   | Role assignment request (Set\<Roles\>)         |
| `FakultasReq`            | Request   | Fakultas CRUD request (code + name)            |
| `AuthenticationResult`   | Internal  | Internal auth result (accessToken, refreshToken + expiration) |
| `AuthenticationRes`      | Response  | API response login/register (accessToken + expiration) |
| `TokenRes`               | Response  | API response token refresh (accessToken)       |
| `UserRes`                | Response  | User info response (id, name, email, roles)    |
| `FakultasRes`            | Response  | Fakultas response (id, code, name)             |

##### Enums

| Enum         | Values                                                  |
|--------------|---------------------------------------------------------|
| `Roles`      | `USER`, `ADMIN`, `SUPER_ADMIN`                          |
| `Permissions`| 13 permissions: `user:read/create/update/delete`, `user:assign-role`, `role:read/create/update/delete`, `fakultas:read/create/update/delete` |
| `TokenType`  | `BEARER`                                                |

##### Repositories

| Repository        | Key Custom Methods                                                                  |
|-------------------|-------------------------------------------------------------------------------------|
| `UserRepo`        | `findByEmail()`, `existsByEmail()`                                                 |
| `RoleRepo`        | `findByRole()`, `findByRoleIn()`                                                   |
| `PermissionRepo`  | `findByName()`                                                                     |
| `RefreshTokenRepo`| `findByToken()`, `revokeAllUserTokens()`, `findAllByUserAndRevokedFalse()`, `deleteByExpiredBefore()` |

##### Services

| Service                  | Description                                                                |
|--------------------------|----------------------------------------------------------------------------|
| `AuthService`            | Interface: register, login, loginAndLogoutForAllDevices, getNewAccessToken, logoutAllDevices, logoutThisDevice |
| `AuthServiceImpl`        | Implementasi: validasi email duplicate, encode password, generate tokens, revoke tokens |
| `AccessJwtService`       | Generate & validate JWT access tokens (HMAC-SHA256)                        |
| `RefreshTokenService`    | Generate, validate, revoke refresh tokens; scheduled cleanup expired tokens |
| `TokenHashingService`    | SHA-256 hashing untuk refresh token sebelum disimpan ke database           |
| `CustomUserDetailsService` | Load user by email, build GrantedAuthority dari roles + permissions       |
| `UserService`            | Interface: get users, update roles                                          |
| `UserServiceImpl`        | List all users, update user roles dengan validasi                           |

##### Security Components

| Component                      | Description                                                          |
|--------------------------------|----------------------------------------------------------------------|
| `SecurityConfig`              | Filter chain config: stateless sessions, CSRF disabled, JWT filter, BCrypt, RoleHierarchy |
| `JwtAuthenticationFilter`     | OncePerRequestFilter: extract Bearer token, validate, set SecurityContext |
| `JwtAuthenticationEntryPoint` | Returns HTTP 401 on authentication failure                           |
| `JwtAccessDeniedHandler`      | Returns HTTP 403 on access denied                                    |

##### Controllers

| Controller          | Base Path          | Endpoints                                                     |
|---------------------|--------------------|---------------------------------------------------------------|
| `ApiAuthController` | `/api/v1/auth`     | POST `/register`, `/login`, `/login-logout`, `/refresh-token`, `/logout`, `/logout-all-devices` |
| `ApiUserController` | `/api/v1/users`    | GET `/` (ROLE_USER), POST `/{userId}/roles` (ROLE_SUPER_ADMIN) |

#### 4.1.3 Business Rules

1. **Registration**: Email harus unik. Password di-encode dengan BCrypt. Default role: `USER`. Refresh token disimpan di HTTP-only cookie.
2. **Login**: Autentikasi via `AuthenticationManager`. Access token dikembalikan di JSON body. Refresh token di HTTP-only cookie.
3. **Login-Logout (All Devices)**: Sama seperti login, tetapi semua refresh token sebelumnya di-revoke terlebih dahulu (token rotation).
4. **Refresh Token**: Access token baru dihasilkan berdasarkan refresh token yang valid (tidak revoked, belum expired).
5. **Logout (This Device)**: Refresh token device saat ini di-revoke.
6. **Logout (All Devices)**: Semua refresh token user di-revoke.
7. **Refresh Token Hashing**: Refresh token di-hash dengan SHA-256 sebelum disimpan ke database. Raw token hanya dikirim ke client.
8. **Scheduled Cleanup**: Expired refresh tokens dihapus setiap hari pada jam 3 pagi.

---

### 4.2 Modul Fakultas (`master.fakultas`)

#### 4.2.1 Purpose

Modul CRUD contoh untuk data Fakultas dengan audit trail dan soft delete.

#### 4.2.2 Components

| Component         | Description                                                |
|-------------------|------------------------------------------------------------|
| `FakultasEntity`  | JPA entity (`t_fakultas`), extends `BaseAuditableSoftDelete`. Fields: id, code, name |
| `FakultasReq`     | Request DTO: code + name                                   |
| `FakultasRes`     | Response DTO: id + code + name                             |
| `FakultasRepo`    | `JpaRepository<FakultasEntity, String>`                    |
| `FakultasService` | Interface: get, getById, save, update, delete               |
| `FakultasServiceImpl` | Hanya `get()` yang diimplementasi. Lainnya stub (Optional.empty()) |
| `FakultasController` | REST controller dengan `@PreAuthorize` per endpoint       |

#### 4.2.3 API Endpoints

| Method | Path               | Permission Required   | Description      |
|--------|--------------------|-----------------------|------------------|
| GET    | `/api/v1/fakultas`       | `fakultas:read`   | List all fakultas|
| GET    | `/api/v1/fakultas/{id}`  | `fakultas:read`   | Get by ID (stub) |
| POST   | `/api/v1/fakultas`       | `fakultas:create` | Create (stub)    |
| PUT    | `/api/v1/fakultas/{id}`  | `fakultas:update` | Update (stub)    |
| DELETE | `/api/v1/fakultas/{id}`  | `fakultas:update` | Delete (stub)    |

> **Catatan**: Endpoint DELETE menggunakan permission `fakultas:update` (bukan `fakultas:delete`). Method `getById`, `save`, `update`, `delete` pada `FakultasServiceImpl` masih berupa stub yang mengembalikan `Optional.empty()`.

---

### 4.3 Base Layer (`base`)

| Component                | Description                                                                  |
|--------------------------|------------------------------------------------------------------------------|
| `BaseAuditableSoftDelete`| Abstract `@MappedSuperclass` dengan audit fields (`createdAt/By`, `updatedAt/By`) dan soft delete fields (`deletedAt/By`). `@SQLRestriction("deleted_at IS NULL")` untuk auto-filter. |
| `Response<T>`            | Generic API response wrapper (record): status, message, data. Factory methods: `success()`, `created()`, `updated()`, `deleted()`, `custom()`. |
| `ResponseError`          | Error response wrapper (record): timestamp, status, message, error.          |
| `DataInitializer`        | `CommandLineRunner` yang seed database: permissions, 3 roles, 3 default users. |

---

### 4.4 Utility Layer (`util`)

| Component          | Description                                                                |
|--------------------|----------------------------------------------------------------------------|
| `CommonUtil`       | Generate UUID tanpa dash untuk entity ID                                   |
| `CookieUtil`       | Manage HTTP-only refresh token cookies (add + delete)                      |
| `JwtKeyGenerator`  | Standalone utility untuk generate HMAC-SHA256 secret keys (Base64)         |

---

## 5. API Endpoints

### 5.1 Authentication Endpoints (`/api/v1/auth`)

Semua endpoint di bawah **tidak memerlukan autentikasi** (public).

| Method | Path                    | Request Body               | Response Body             | Cookie           | Description                    |
|--------|-------------------------|----------------------------|---------------------------|------------------|--------------------------------|
| POST   | `/register`             | `RegistrationReq`          | `Response<AuthenticationRes>` | refresh_token  | Register user baru             |
| POST   | `/login`                | `AuthenticationReq`        | `Response<AuthenticationRes>` | refresh_token  | Login (multi-device)           |
| POST   | `/login-logout`         | `AuthenticationReq`        | `Response<AuthenticationRes>` | refresh_token  | Login & revoke all lainnya     |
| POST   | `/refresh-token`        | - (cookie: refresh_token)  | `Response<TokenRes>`         | -              | Refresh access token           |
| POST   | `/logout`               | - (cookie: refresh_token)  | `Response<String>`           | (delete)       | Logout this device             |
| POST   | `/logout-all-devices`   | - (cookie: refresh_token)  | `Response<String>`           | (delete)       | Logout all devices             |

### 5.2 User Endpoints (`/api/v1/users`)

| Method | Path                    | Permission Required     | Request Body  | Response Body                | Description             |
|--------|-------------------------|------------------------|---------------|------------------------------|-------------------------|
| GET    | `/`                     | `ROLE_USER`             | -             | `Response<List<UserRes>>`   | List all users          |
| POST   | `/{userId}/roles`       | `ROLE_SUPER_ADMIN`     | `RolesReq`    | `Response<String>`           | Assign roles to user    |

### 5.3 Fakultas Endpoints (`/api/v1/fakultas`)

| Method | Path                    | Permission Required     | Request Body  | Response Body                 | Description             |
|--------|-------------------------|------------------------|---------------|------------------------------|-------------------------|
| GET    | `/`                     | `fakultas:read`         | -             | `Response<List<FakultasRes>>`| List all fakultas       |
| GET    | `/{id}`                 | `fakultas:read`         | -             | `Response<Optional<FakultasRes>>` | Get by ID (stub)  |
| POST   | `/`                     | `fakultas:create`       | `FakultasReq` | `Response<Optional<FakultasRes>>` | Create (stub)    |
| PUT    | `/{id}`                 | `fakultas:update`       | `FakultasReq` | `Response<Optional<FakultasRes>>` | Update (stub)    |
| DELETE | `/{id}`                 | `fakultas:update`       | -             | `Response<Optional<FakultasRes>>` | Delete (stub)    |

---

## 6. Database Schema

### 6.1 Entity Relationship Diagram

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│     t_user       │     │  t_user_roles    │     │     t_roles      │
├──────────────────┤     ├──────────────────┤     ├──────────────────┤
│ uid (PK)         │────<│ user_id (FK)     │     │ rid (PK)         │
│ first_name       │     │ roles_id (FK)    │>────│ role (ENUM)      │
│ last_name        │     └──────────────────┘     └────────┬─────────┘
│ email            │                                        │
│ password         │     ┌──────────────────┐              │
└────────┬─────────┘     │role_permissions  │              │
         │               ├──────────────────┤              │
         │               │ role_id (FK)     │>─────────────┘
         │               │ permission_id(FK)│>─────────────┐
         │               └──────────────────┘              │
         │                                                 │
         │               ┌──────────────────┐     ┌───────┴──────────┐
         │               │     t_token      │     │  t_permissions   │
         │               ├──────────────────┤     ├──────────────────┤
         └──────────────>│ user_id (FK)     │     │ pid (PK)         │
                         │ tid (PK)         │     │ name (ENUM)      │
                         │ token (UNIQUE)   │     └──────────────────┘
                         │ expired          │
                         │ revoked          │
                         └──────────────────┘

┌──────────────────────────────────────────┐
│              t_fakultas                   │
├──────────────────────────────────────────┤
│ id (PK)                                  │
│ code                                     │
│ name                                     │
│ created_at, created_by (audit)           │
│ updated_at, updated_by (audit)           │
│ deleted_at, deleted_by (soft delete)     │
└──────────────────────────────────────────┘
```

### 6.2 Table Definitions

#### `t_user`
| Column      | Type         | Constraint          |
|-------------|--------------|----------------------|
| uid         | VARCHAR(36)  | PK                   |
| first_name  | VARCHAR(64)  |                      |
| last_name   | VARCHAR(64)  |                      |
| email       | VARCHAR(100) |                      |
| password    | VARCHAR(64)  |                      |

#### `t_roles`
| Column | Type         | Constraint |
|--------|--------------|------------|
| rid    | VARCHAR(36)  | PK         |
| role   | ENUM         | UNIQUE     |

#### `t_permissions`
| Column | Type         | Constraint |
|--------|--------------|------------|
| pid    | VARCHAR(36)  | PK         |
| name   | VARCHAR(64)  | UNIQUE, NOT NULL |

#### `t_user_roles`
| Column   | Type         | Constraint                |
|----------|--------------|----------------------------|
| user_id  | VARCHAR(36)  | FK → t_user.uid            |
| roles_id | VARCHAR(36)  | FK → t_roles.rid           |

#### `t_role_permissions`
| Column        | Type         | Constraint                     |
|---------------|--------------|--------------------------------|
| role_id       | VARCHAR(36)  | FK → t_roles.rid               |
| permission_id | VARCHAR(36)  | FK → t_permissions.pid          |

#### `t_token`
| Column  | Type         | Constraint              |
|---------|--------------|--------------------------|
| tid     | VARCHAR(36)  | PK                       |
| token   | VARCHAR      | UNIQUE                   |
| expired | TIMESTAMP    |                          |
| revoked | BOOLEAN      |                          |
| user_id | VARCHAR(36)  | FK → t_user.uid          |

#### `t_fakultas`
| Column     | Type         | Constraint              |
|------------|--------------|--------------------------|
| id         | VARCHAR      | PK                       |
| code       | VARCHAR      |                          |
| name       | VARCHAR      |                          |
| created_at | TIMESTAMP    | NOT NULL, DEFAULT NOW()  |
| created_by | VARCHAR      |                          |
| updated_at | TIMESTAMP    |                          |
| updated_by | VARCHAR      |                          |
| deleted_at | TIMESTAMP    |                          |
| deleted_by | VARCHAR      |                          |

---

## 7. Security Configuration

### 7.1 Filter Chain

```
Request → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → Controller
```

- **CSRF**: Disabled (stateless API)
- **Session**: STATELESS
- **Public Endpoints**: `/api/v1/auth/**`
- **Authenticated Endpoints**: All other paths

### 7.2 JWT Configuration

| Parameter               | Value                                    |
|-------------------------|------------------------------------------|
| Algorithm               | HMAC-SHA256                              |
| Access Token Expiration | 15 minutes                               |
| Refresh Token Expiration| 1440 minutes (24 hours)                  |
| Access Secret           | Base64 encoded (configurable)            |
| Refresh Secret          | Base64 encoded (configurable)            |
| Token Storage           | Refresh token hashed (SHA-256) in DB     |
| Cookie                  | HTTP-only, SameSite=Strict, Path=/       |

### 7.3 Role Hierarchy

```
SUPER_ADMIN → ADMIN → USER
```

- `SUPER_ADMIN` implicitly has all `ADMIN` and `USER` permissions
- `ADMIN` implicitly has all `USER` permissions

### 7.4 Role-Permission Mapping

| Role        | Permissions                                                                                   |
|-------------|-----------------------------------------------------------------------------------------------|
| USER        | `fakultas:read`                                                                               |
| ADMIN       | `fakultas:read`, `fakultas:create`, `fakultas:update`, `fakultas:delete`, `user:read`, `user:assign-role` |
| SUPER_ADMIN | All 13 permissions                                                                            |

---

## 8. Error Handling

### 8.1 Global Exception Handler (`GlobalExceptionHandler`)

| Exception                          | HTTP Status | Description                    |
|------------------------------------|-------------|--------------------------------|
| `MethodArgumentNotValidException`  | 400         | Validation errors              |
| `BadRequestException`              | 400         | Bad request                    |
| `AuthenticationException`          | 401         | Authentication failed          |
| `JwtAuthenticationException`       | 401         | JWT authentication failed      |
| `PaymentServiceException`          | 402         | Payment error                  |
| `AccessDeniedException`            | 403         | Insufficient permissions       |
| `NotFoundException`                | 404         | Resource not found             |
| `DuplicateException`               | 409         | Resource already exists        |
| `DataAccessException`             | 500         | Database error                 |
| `Exception` (generic)             | 500         | Internal server error          |

### 8.2 Error Response Format

```json
{
  "timestamp": "2026-07-27T10:00:00",
  "status": 400,
  "message": "Bad Request",
  "error": "Error details"
}
```

### 8.3 Success Response Format

```json
{
  "status": 200,
  "message": "Success",
  "data": { ... }
}
```

---

## 9. Non-Functional Requirements

### 9.1 Performance

| Metric                        | Target             |
|-------------------------------|--------------------|
| Access Token Generation      | < 50ms             |
| Refresh Token Validation     | < 100ms            |
| JWT Filter Overhead          | < 10ms per request |
| API Response Time (avg)      | < 200ms            |

### 9.2 Security

| Requirement                   | Implementation                                     |
|-------------------------------|----------------------------------------------------|
| Password Hashing             | BCrypt (Spring Security default)                   |
| Token Storage                | Refresh tokens hashed with SHA-256                 |
| Transmission Security        | HTTP-only cookies, SameSite=Strict                 |
| Stateless Sessions           | No server-side session storage                     |
| CSRF Protection              | Disabled (API-only, token-based auth)              |
| Secret Key Management        | Externalized via environment variables (`JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`) |

### 9.3 Reliability

| Requirement                   | Implementation                                     |
|-------------------------------|----------------------------------------------------|
| Token Expiration             | Access: 15 min, Refresh: 24 hours                  |
| Token Revocation             | Server-side via database flag                      |
| Scheduled Cleanup            | Daily at 3:00 AM for expired tokens                |
| Soft Delete                  | Audit trail preserved via `deleted_at` timestamp   |

### 9.4 Scalability Considerations

| Aspect                        | Current Approach                                   |
|-------------------------------|----------------------------------------------------|
| Database                      | MySQL single instance                              |
| Session Management            | Stateless (JWT)                                    |
| Token Storage                 | RDBMS (t_token table)                              |
| Horizontal Scaling            | Supported (stateless architecture)                 |

---

## 10. Default Seed Data

### 10.1 Permissions (13 entries)

`user:read`, `user:create`, `user:update`, `user:delete`, `user:assign-role`, `role:read`, `role:create`, `role:update`, `role:delete`, `fakultas:read`, `fakultas:create`, `fakultas:update`, `fakultas:delete`

### 10.2 Roles (3 entries)

| Role        | Permission Set                                          |
|-------------|---------------------------------------------------------|
| USER        | `fakultas:read`                                         |
| ADMIN       | `fakultas:read`, `fakultas:create`, `fakultas:update`, `fakultas:delete`, `user:read`, `user:assign-role` |
| SUPER_ADMIN | All 13 permissions                                      |

### 10.3 Default Users

| Email                    | Password      | Role        |
|--------------------------|---------------|-------------|
| superadmin@guvaren.com   | superadmin    | SUPER_ADMIN |
| admin@guvaren.com        | admin         | ADMIN       |
| user@guvaren.com         | user          | USER        |

---

## 11. Known Issues & Technical Debt

| #  | Issue                                                                 | Severity | Status |
|----|-----------------------------------------------------------------------|----------|--------|
| 1  | JWT secrets hardcoded di `application.yaml`                           | High     | **Resolved** - Externalized via `${JWT_ACCESS_SECRET}` / `${JWT_REFRESH_SECRET}` env vars |
| 2  | Hibernate DDL mode = `create` (drop & recreate on restart)            | High     | **Resolved** - Changed to `ddl-auto: update` |
| 3  | `FakultasServiceImpl`: `getById()`, `save()`, `update()`, `delete()` masih stub | Medium | **Resolved** - Full CRUD implementation with NotFoundException |
| 4  | `FakultasController` DELETE menggunakan `fakultas:update` permission (seharusnya `fakultas:delete`) | Medium | **Resolved** - Already correct (`fakultas:delete`) |
| 5  | Response DTO untuk Fakultas GET by ID menggunakan `Optional<>` wrapper yang tidak ideal | Low | **Resolved** - Removed Optional wrappers from service and controller |
| 6  | Tidak ada rate limiting pada endpoint autentikasi                     | Medium | **Resolved** - Added `RateLimitFilter` (30 req/min sliding window) on `/api/v1/auth/**` |
| 7  | Tidak ada input sanitization/pagination pada list endpoints           | Low      | **Resolved** - Added `Pageable` with `@PageableDefault(size = 20)` on all list endpoints |
| 8  | `RefreshTokenEntity` menggunakan public fields daripada private       | Low      | **Resolved** - Fields already private with Lombok `@Getter/@Setter` |
| 9  | Tidak ada HTTPS enforcement pada konfigurasi cookie                   | Medium   | **Resolved** - `cookie.secure` defaults to `true` via `${COOKIE_SECURE:true}` env var |
| 10 | `PaymentServiceException` didefinisikan tetapi tidak digunakan dalam bisnis flow | Low | **Resolved** - Removed class and its GlobalExceptionHandler method |

**Last resolved: 2026-07-27**

---

## 12. Dependencies

### 12.1 Runtime Dependencies

| Dependency                           | Version | Scope    |
|--------------------------------------|---------|----------|
| spring-boot-starter-data-jpa         | 4.1.0   | compile  |
| spring-boot-starter-jdbc             | 4.1.0   | compile  |
| spring-boot-starter-security         | 4.1.0   | compile  |
| spring-boot-starter-validation       | 4.1.0   | compile  |
| spring-boot-starter-webmvc           | 4.1.0   | compile  |
| jjwt-api                             | 0.13.0  | compile  |
| jjwt-impl                            | 0.13.0  | runtime  |
| jjwt-jackson                         | 0.13.0  | runtime  |
| mysql-connector-j                    | -       | runtime  |
| lombok                               | -       | provided |
| spring-boot-docker-compose           | -       | runtime  |

### 12.2 Test Dependencies

| Dependency                           | Version | Scope    |
|--------------------------------------|---------|----------|
| spring-boot-starter-data-jpa-test    | 4.1.0   | test     |
| spring-boot-starter-jdbc-test        | 4.1.0   | test     |
| spring-boot-starter-security-test    | 4.1.0   | test     |
| spring-boot-starter-validation-test  | 4.1.0   | test     |
| spring-boot-starter-webmvc-test      | 4.1.0   | test     |

---

## 13. Configuration Reference

### 13.1 application.yaml

```yaml
spring:
  application:
    name: spring-security-jwt-exercise
  datasource:
    url: jdbc:mysql://localhost:3306/mydatabase
    username: myuser
    password: verysecret
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        jdbc:
          time_zone: Asia/Jakarta
app:
  jwt:
    access-secret: ${JWT_ACCESS_SECRET:default-access-secret-change-in-prod}
    refresh-secret: ${JWT_REFRESH_SECRET:default-refresh-secret-change-in-prod}
    access-expiration: 15
    refresh-expiration: 1440
  cookie:
    secure: ${COOKIE_SECURE:true}
    max-age: 1440
```

### 13.2 Docker Compose

```yaml
services:
  mysql:
    image: 'mysql:latest'
    container_name: 'mysql'
    environment:
      - 'MYSQL_DATABASE=mydatabase'
      - 'MYSQL_PASSWORD=secret'
      - 'MYSQL_ROOT_PASSWORD=verysecret'
      - 'MYSQL_USER=myuser'
    ports:
      - '3306:3306'
```

---

## 14. Testing Strategy

### 14.1 Unit Tests

| Test Class                  | Target                  | Type   |
|-----------------------------|------------------------|--------|
| `TokenHashingServiceTest`   | SHA-256 hashing        | Unit   |
| `CommonUtilTest`            | UUID generation        | Unit   |
| `ResponseTest`              | Response wrapper       | Unit   |
| `ResponseErrorTest`         | Error response         | Unit   |
| `CustomStatusTest`          | Enum values            | Unit   |
| `AccessJwtServiceTest`      | JWT generate/validate  | Unit   |
| `RefreshTokenServiceTest`   | Refresh token ops      | Unit   |
| `CustomUserDetailsServiceTest`| User details loading | Unit   |
| `AuthServiceImplTest`       | Auth business logic    | Unit   |
| `UserServiceImplTest`       | User service logic     | Unit   |
| `FakultasServiceImplTest`   | Fakultas service logic | Unit   |
| `GlobalExceptionHandlerTest`| Exception mapping      | Unit   |
| `ApiAuthControllerTest`     | Auth REST endpoints    | Unit   |
| `ApiUserControllerTest`     | User REST endpoints    | Unit   |
| `FakultasControllerTest`    | Fakultas REST endpoints| Unit   |
| `RolesTest`                 | Role enum validation   | Unit   |
| `PermissionsTest`           | Permissions enum       | Unit   |

### 14.2 Testing Tools

- **JUnit 5** - Test framework
- **Mockito** - Mocking dependencies
- **MockMvc** - HTTP layer testing
- **AssertJ** - Fluent assertions

---

*Document generated on: 2026-07-27*
*Version: 1.1*
*Last updated: 2026-07-27 - All 10 issues in Section 11 resolved*
