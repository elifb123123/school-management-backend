# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Spring Boot 4.1.0 (Java 21) learning/demo REST API modeling Schools, Teachers, Students, and the `User` accounts
(with `PRINCIPAL`/`TEACHER`/`STUDENT` roles) that own them. Note: many source comments and validation messages are in
Turkish, and several files contain `// TODO:` learning-notes left by the author (e.g. "DEPENDENCY INJECTION NEDİR?")
— these are intentional study notes, not stale cruft to delete.

Significant architectural decisions (and the alternatives that were rejected) are recorded as ADRs under
`docs/adr/` — check there before re-litigating something like "why isn't there a separate RegistrationService" or
"why doesn't Teacher have its own email field."

## Commands

Windows environment — use `mvnw.cmd`, not `./mvnw`.

- Build: `mvnw.cmd clean install`
- Run app: `mvnw.cmd spring-boot:run`
- Run all tests: `mvnw.cmd test`
- Run a single test class: `mvnw.cmd test -Dtest=DemoApplicationTests`
- Run a single test method: `mvnw.cmd test -Dtest=DemoApplicationTests#contextLoads`

There is currently only a context-load smoke test (`src/test/java/com/example/demo/DemoApplicationTests.java`) — no
service/controller/repository test coverage exists yet.

The Maven annotation-processor chain in `pom.xml` is order-sensitive: Lombok, then `lombok-mapstruct-binding`, then
the MapStruct processor, in that exact order (there's a comment in `pom.xml` noting this was already fixed once
after breaking). If Lombok-generated getters/setters stop being visible to MapStruct (mappers silently produce
null/unmapped fields), check this ordering before anything else.

## Database configuration

`src/main/resources/application.properties` points at Postgres with `spring.jpa.hibernate.ddl-auto=create` — **the
schema is dropped and recreated on every
application start**, and a running Postgres instance on localhost is required. H2 and `spring-boot-h2console` are on the
classpath (runtime scope) but Postgres is the active `spring.datasource.url`, so switch that property if you want to run
against H2 instead.

`DataConfig` (`src/main/java/com/example/demo/config/DataConfig.java`) seeds two schools, two students (`ayse`,
`alex`), and two teachers (`john.smith`, `maria.garcia`) via a `CommandLineRunner` if the tables are empty on
startup — useful reference data when testing endpoints manually (e.g. via `generated-requests.http`). Since every
`Teacher`/`Student` must own a `User` account (see below), the seeder creates them through
`UserService.registerTeacher`/`registerStudent` rather than constructing `Teacher`/`Student` entities directly — all
seeded accounts use the password `"password"`.

## Architecture

The app is organized **package-by-feature**, not package-by-layer: each domain (`school`, `teacher`, `student`,
`user`) under `src/main/java/com/example/demo/` has its own `controller/`, `dto/`, `mapper/`, `persistence/` (entity +
`Repository` + `specification/`), and `service/` (interface + `*ServiceImpl`). Cross-cutting concerns live in the
top-level `exception/`, `config/`, and `security/` packages.

Per-domain pattern to follow when adding a new domain or endpoint:

1. `persistence/Entity.java` — JPA entity (Lombok `@Getter @Setter`, no `@Data`)
2. `persistence/EntityRepository.java` — extends `JpaRepository` + `JpaSpecificationExecutor` for dynamic filtering
3. `persistence/specification/EntitySpecification.java` — static `Specification<T>` factory methods, one per filterable
   field, each returning `null` when the filter arg is blank/null so it's a no-op in the combined spec
4. `dto/EntityRequest.java` (record, with `jakarta.validation` annotations) and `dto/EntityResponse.java` (record)
5. `mapper/EntityMapper.java` — MapStruct interface (`@Mapper(componentModel = "spring")`); fields requiring a
   repository lookup (e.g. resolving a `School` by name) are `@Mapping(target = ..., ignore = true)` and filled in by
   the service layer instead
6. `service/EntityService.java` (interface) + `service/EntityServiceImpl.java` (`@Service @Transactional`, constructor
   injection, `@Slf4j` logging on every mutation)
7. `controller/EntityController.java` — thin `@RestController`, delegates directly to the service, returns DTOs/
   `Page<DTO>` (list endpoints take `Pageable` plus optional `@RequestParam` filters)

### Identity vs. domain profile (`user`, `security`)

`User` (`user/persistence/User.java`) holds login identity only — `username`, `email`, `password` (BCrypt-hashed),
and a `Role` enum (`PRINCIPAL`, `TEACHER`, `STUDENT`). `Teacher` and `Student` hold **only** their own domain-specific
fields (`branch`/`school` for Teacher, `dateOfBirth`/`school` for Student) — they do **not** duplicate `name`/`email`;
those are read through the mandatory, cascading `@OneToOne` link to `User` (`Teacher.user` / `Student.user`,
`nullable = false`, `cascade = CascadeType.ALL`, `orphanRemoval = true`). `TeacherResponse`/`StudentResponse` still
expose `name`/`email` in their JSON shape — `TeacherMapper`/`StudentMapper` populate them via
`@Mapping(source = "user.username", ...)` / `@Mapping(source = "user.email", ...)`, not from a field on the entity
itself. See `docs/adr/0001-identity-vs-domain-profile-split.md` for why.

There is deliberately **no standalone "create a bare Teacher/Student" endpoint** — the old `POST /api/teacher` was
removed. The only way to create a `Teacher` or `Student` is through registration (below), which guarantees every one
has a linked `User` and prevents "orphan" domain records.

### Registration flow

`POST /api/register/{principal,teacher,student}` (`user/controller/UserController.java`) are the only entry points
that create `User`s (and, for teacher/student, the linked domain entity) together, atomically:

- `UserService`/`UserServiceImpl.register{Teacher,Student}` builds and saves the `User` first (setting the
  appropriate `Role`, rejecting duplicate emails via `userRepository.existsByEmail(...)` →
  `ResourceAlreadyExistsException`), **then** calls `TeacherService.registerTeacher(TeacherRequest, User)` /
  `StudentService.registerStudent(StudentRequest, User)` with the already-persisted `User` — never the other way
  around, and never before the `User` has an id. The whole method is `@Transactional` so a failure on the
  domain-entity side (e.g. bad `schoolId`) rolls back the `User` insert too.
- `TeacherService.registerTeacher` / `StudentService.registerStudent` own the actual entity-construction logic
  (resolving `School`, setting the `user` association) — `UserServiceImpl` never builds a `Teacher`/`Student` itself,
  it only orchestrates. This registration logic intentionally lives inside `UserServiceImpl` rather than a separate
  `RegistrationService`; see `docs/adr/0002-registration-orchestration-location.md`.
- Composite request DTOs live in `user/dto/` and wrap the existing per-domain request records instead of redeclaring
  fields: `TeacherRegistrationRequest(UserRequest, TeacherRequest)`, `StudentRegistrationRequest(UserRequest,
  StudentRequest)`.

### Security (`security/`)

`SecurityConfig` and `UserDetailServiceImpl` live in `security/`, not `config/` — they're framework-integration code,
distinct from generic app config and from the `user` domain's own CRUD logic. `UserDetailServiceImpl` loads real
`User` rows via `UserRepository.findByUsername`; there's no fallback `spring.security.user.*` account. Authorities
are built as `"ROLE_" + role.name()` (never `.toString()` — `name()` is `final` on `Enum`, `toString()` is
overridable and would silently break the authority string if someone customized it later).

Rules (`SecurityConfig.filterChain`): `/api/register/principal` is `permitAll()` (bootstrapping — there would
otherwise be no way to create the first account), `/api/register/teacher` and `/api/register/student` require
`hasRole("PRINCIPAL")`, everything else requires `authenticated()`. See
`docs/adr/0003-role-based-registration-access.md` for why registration isn't self-service for teacher/student.

### Cross-domain relationships

- `Student.school` / `Teacher.school` are `@ManyToOne` onto `School`; `SchoolRepository` exposes `findStudentsById`/
  `findTeachersById` as paged JPQL queries for the school's sub-resource endpoints. `School.students`/`School.teachers`
  are the inverse `@OneToMany` sides with `cascade = CascadeType.ALL, orphanRemoval = true` — **deleting a `School`
  cascades to delete every `Student`/`Teacher` at that school**, which (per the identity split above) cascades further
  and deletes their `User` accounts too. This is intentional: a school going away should take its accounts with it,
  not leave orphaned students/teachers/logins behind. There's no soft-delete or reassignment step, so this is
  irreversible — keep that in mind if a "deactivate" or "transfer to another school" flow is ever needed instead of
  a hard delete.
- The Student↔Teacher relationship is `@ManyToMany`, owned by `Teacher` (`Teacher.students`, join table
  `student_teacher`); `Student.teachers` is the inverse (`mappedBy`) side. Because of this, **all relation mutations (
  link/unlink/existence checks) live in `TeacherRepository`/`TeacherService`** (native SQL queries against
  `student_teacher`), and `StudentServiceImpl` delegates to `TeacherService` for its own `/{studentId}/teachers/...`
  sub-resource endpoints rather than touching the join table directly. Keep this direction when touching relation
  logic — don't add join-table queries to the student side.
- `User` does **not** carry inverse `@OneToOne(mappedBy = "user")` navigation fields back to `Teacher`/`Student` —
  they were tried and removed: nothing in the codebase used them, `@OneToOne` defaults to `EAGER`, and it would have
  added an unconditional extra query to every `User` load (including every basic-auth check in
  `UserDetailServiceImpl.loadUserByUsername`). Add them back as `FetchType.LAZY`, with both sides of the association
  kept in sync on write, only when something actually needs `user.getTeacher()`/`user.getStudent()` navigation.

### Errors

`GlobalExceptionHandler` (`@RestControllerAdvice`) is the single place mapping exceptions to RFC 7807 `ProblemDetail`
responses: `ResourceNotFoundException` → 404, `ResourceAlreadyExistsException` → 409,
bean-validation/type-mismatch/unreadable-body → 400, catch-all → 500. Throw the domain exceptions from `exception/` (
constructed as `new ResourceNotFoundException("Entity", "field", value)`) from service methods rather than handling
not-found/conflict cases ad hoc in controllers. Note that a raw DB-level unique-constraint violation (e.g. inserting a
`User` without the `existsByEmail` pre-check) surfaces as an unhandled `DataIntegrityViolationException` → generic
500 via the catch-all, not a clean 409 — always add the proactive `existsBy...` check + `ResourceAlreadyExistsException`
in the service rather than relying on the DB constraint alone to produce a good error response.

### CORS

`CorsConfig` (`config/CorsConfig.java`) allows `http://localhost:*` origins (any localhost port) with
GET/POST/PUT/DELETE/PATCH/OPTIONS and all headers, scoped to `/api/**`. Update this if a frontend is served from a
non-localhost origin.

### Filtering & pagination

List endpoints combine `Specification`s built from optional query params. Note the existing student/teacher/school
combinators use `Specification.where(...).or(...)` when chaining multiple optional filters (e.g.
`StudentServiceImpl.getStudents`) — be aware this means providing multiple filters is OR'd, not AND'd, which is likely
not the intended search semantics; check current behavior before assuming AND-filtering when adding new filtered fields.