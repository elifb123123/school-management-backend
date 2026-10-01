# School Management Backend

REST API for managing schools, principals, teachers, students, and the relationships between teachers and students. The application is implemented with Spring Boot and exposes JSON endpoints under `/api`.

## Project overview

The backend provides:

- Principal, teacher, and student registration
- Email/password login with BCrypt password hashing
- JWT access tokens and database-backed refresh-token rotation
- School, teacher, and student profile management
- Teacher/student linking and unlinking
- Role- and ownership-based authorization
- Pagination and optional filters on collection endpoints
- RFC 7807-style `ProblemDetail` error responses through a global exception handler

The backend is designed to be consumed by a separate frontend application.

## Technology stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL JDBC driver
- H2 runtime dependency and Spring Boot H2 console dependency
- JSON Web Tokens with JJWT 0.12.6
- MapStruct 1.5.5.Final
- Lombok 1.18.42
- Jakarta Bean Validation
- Maven Wrapper

## Architecture and project structure

The code is organized by feature rather than by technical layer:

```text
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── auth/          # Login, refresh, logout, and refresh-token persistence
│   │   ├── config/        # CORS configuration and startup seed data
│   │   ├── exception/     # Domain exceptions and global ProblemDetail handling
│   │   ├── school/        # School controller, DTOs, entity, repository, mapper, service
│   │   ├── security/      # JWT service/filter and Spring Security configuration
│   │   ├── student/       # Student feature and student-teacher sub-resources
│   │   ├── teacher/       # Teacher feature and teacher-student sub-resources
│   │   └── user/          # User identity, roles, registration, and current-user endpoint
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/demo/
        └── DemoApplicationTests.java
```

Each domain generally contains `controller`, `dto`, `mapper`, `persistence`, and `service` packages. School, teacher, and student authorization helpers are kept in each domain's `security` package. MapStruct is used for entity/DTO mapping, while services handle repository lookups and relationship changes.

## API overview

The default base URL is `http://localhost:8080`.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Authenticate with email and password; returns an access token and refresh token |
| `POST` | `/api/auth/refresh` | Rotate a valid refresh token and issue a new token pair |
| `POST` | `/api/auth/logout` | Revoke a refresh token |

`LoginRequest` contains `email` and `password`. Refresh and logout requests contain `refreshToken`.

### Registration and current user

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/register/principal` | Register a principal and create the principal's school |
| `POST` | `/api/register/teacher` | Register a teacher for a school; requires a principal with ownership of that school |
| `POST` | `/api/register/student` | Register a student for a school; requires a principal with ownership of that school |
| `GET` | `/api/me` | Return the authenticated user's role, domain entity ID, and name |

Registration requests use nested objects:

```json
{
  "userRequest": {
    "name": "Principal name",
    "email": "principal@example.com",
    "password": "password"
  },
  "schoolRequest": {
    "schoolName": "Example School",
    "address": "123 Main Street"
  }
}
```

Teacher registration replaces `schoolRequest` with `teacherRequest` (`branch` and `schoolId`), and student registration replaces it with `studentRequest` (`dateOfBirth` in ISO format and `schoolId`).

There are no standalone create endpoints for schools, teachers, or students. These records are created through the registration endpoints so that each domain record is linked to a user account.

### Schools

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/school` | Paginated school list; optional `name` filter |
| `GET` | `/api/school/{schoolId}` | Get a school |
| `PUT` | `/api/school/{schoolId}` | Update a school |
| `DELETE` | `/api/school/{schoolId}` | Delete a school |
| `GET` | `/api/school/{schoolId}/students` | Paginated students in a school |
| `GET` | `/api/school/{schoolId}/teachers` | Paginated teachers in a school |

### Teachers

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/teacher` | Paginated teacher list; optional `name` filter |
| `GET` | `/api/teacher/{id}` | Get a teacher |
| `PUT` | `/api/teacher/{id}` | Update a teacher |
| `DELETE` | `/api/teacher/{id}` | Delete a teacher |
| `GET` | `/api/teacher/{id}/students` | List students linked to a teacher |
| `POST` | `/api/teacher/{teacherId}/students/{studentId}/link` | Link a student to a teacher |
| `DELETE` | `/api/teacher/{teacherId}/students/{studentId}/unlink` | Unlink a student from a teacher |
| `GET` | `/api/teacher/branches` | List the supported teacher branches |

Supported branches are `MATHEMATICS`, `PHYSICS`, `CHEMISTRY`, `BIOLOGY`, `LITERATURE`, `ENGLISH`, `HISTORY`, and `GEOGRAPHY`.

### Students

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/student` | Paginated student list; optional `name`, `email`, and `birthDate` filters |
| `GET` | `/api/student/{studentId}` | Get a student |
| `PUT` | `/api/student/{studentId}` | Update a student |
| `DELETE` | `/api/student/{studentId}` | Delete a student |
| `POST` | `/api/student/{studentId}/teachers/{teacherId}/link` | Link a teacher to a student |
| `DELETE` | `/api/student/{studentId}/teachers/{teacherId}/unlink` | Unlink a teacher from a student |
| `GET` | `/api/student/{studentId}/teachers` | List teachers linked to a student |

Collection endpoints use Spring pagination parameters such as `page`, `size`, and repeatable `sort=field,asc|desc`.

## Authentication and authorization

Authentication is stateless for normal API requests:

1. Call `/api/auth/login` with a registered email and password.
2. Send the returned access token on protected requests:

   ```http
   Authorization: Bearer <access-token>
   ```

3. Use the refresh token with `/api/auth/refresh` when the access token expires.
4. Use `/api/auth/logout` to revoke a refresh token.

Access tokens are signed JWTs with a 15-minute lifetime. Refresh tokens are opaque UUID values stored in PostgreSQL, expire after one day, and are rotated on refresh. Passwords are stored using BCrypt.

The available roles are `PRINCIPAL`, `TEACHER`, `STUDENT`, and `ADMIN`. Registration creates the first three roles. Principals can manage resources belonging to their own school, while teachers and students have read access to their own profiles and relevant linked resources. The `ADMIN` role is supported by authorization checks, although there is no registration flow that creates an admin account.

The available roles are `PRINCIPAL`, `TEACHER`, `STUDENT`, and `ADMIN`.

- **Self-Registration:** Only `PRINCIPAL` accounts can be created via public registration (which automatically creates the associated school).
- **Managed Creation:** `TEACHER` and `STUDENT` accounts cannot be created independently. They must be registered by an authenticated `PRINCIPAL` who owns the target school.
- **Access Control:** Principals can manage resources belonging to their school, while teachers and students have read-only or limited access to their own profiles and linked resources.

## Database

PostgreSQL is the active database configuration. The default settings point to:

```text
jdbc:postgresql://localhost:5432/mydb
```

JPA is configured with `spring.jpa.hibernate.ddl-auto=create`, so the schema is dropped and recreated when the application starts. Do not use this setting where persistent production data must be preserved.


## Local setup

### Prerequisites

- JDK 21
- PostgreSQL running locally
- A PostgreSQL database named `mydb`

Create or update `src/main/resources/application.properties` (this file is ignored by Git) with the local database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=<your-postgres-password>

spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=${JWT_SECRET:<base64-encoded-secret>}
```

`JWT_SECRET` is the only environment variable read by the application. It should contain a Base64-encoded secret suitable for HMAC signing. The datasource URL, username, and password are currently configured in `application.properties`.

### Run the application

From the repository root on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Build the project:

```powershell
.\mvnw.cmd clean install
```

Run the test suite:

```powershell
.\mvnw.cmd test
```

The API is then available at `http://localhost:8080`.

## CORS

Requests under `/api/**` allow origins matching `http://localhost:*`. Allowed methods are `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, and `OPTIONS`, and all request headers are accepted. Update `CorsConfig` if the frontend is served from a non-localhost origin.

## Frontend

Frontend repository placeholder: [school-management-frontend](https://github.com/your-username/school-management-frontend)
