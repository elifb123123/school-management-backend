# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Spring Boot 4.1.0 (Java 21) learning/demo REST API modeling Schools, Teachers, and Students. Note: many source
comments and validation messages are in Turkish, and several files contain `// TODO:` learning-notes left by the
author (e.g. "DEPENDENCY INJECTION NEDİR?") — these are intentional study notes, not stale cruft to delete.

## Commands

Windows environment — use `mvnw.cmd`, not `./mvnw`.

- Build: `mvnw.cmd clean install`
- Run app: `mvnw.cmd spring-boot:run`
- Run all tests: `mvnw.cmd test`
- Run a single test class: `mvnw.cmd test -Dtest=DemoApplicationTests`
- Run a single test method: `mvnw.cmd test -Dtest=DemoApplicationTests#contextLoads`

There is currently only a context-load smoke test (`src/test/java/com/example/demo/DemoApplicationTests.java`) — no
service/controller/repository test coverage exists yet.

## Database configuration

`src/main/resources/application.properties` points at Postgres — **the schema is dropped and recreated on every
application start**, and a running Postgres instance on localhost is required. H2 and `spring-boot-h2console` are on the
classpath (runtime scope) but Postgres is the active `spring.datasource.url`, so switch that property if you want to run
against H2 instead.

`DataConfig` (`src/main/java/com/example/demo/config/DataConfig.java`) seeds two schools, two students, and two teachers
via a `CommandLineRunner` if the tables are empty on startup — useful reference data when testing endpoints manually (
e.g. via `.idea/httpRequests/*.http`).

The `anthropic.api-key` property in `application.properties` is unused dead config — no code references it.

## Architecture

The app is organized **package-by-feature**, not package-by-layer: each domain (`school`, `teacher`, `student`) under
`src/main/java/com/example/demo/` has its own `controller/`, `dto/`, `mapper/`, `persistence/` (entity + `Repository` +
`specification/`), and `service/` (interface + `*ServiceImpl`). Cross-cutting concerns live in the top-level
`exception/` and `config/` packages.

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

### Cross-domain relationships

- `Student.school` / `Teacher.school` are `@ManyToOne` onto `School`; `SchoolRepository` exposes `findStudentsById`/
  `findTeachersById` as paged JPQL queries for the school's sub-resource endpoints.
- The Student↔Teacher relationship is `@ManyToMany`, owned by `Teacher` (`Teacher.students`, join table
  `student_teacher`); `Student.teachers` is the inverse (`mappedBy`) side. Because of this, **all relation mutations (
  link/unlink/existence checks) live in `TeacherRepository`/`TeacherService`** (native SQL queries against
  `student_teacher`), and `StudentServiceImpl` delegates to `TeacherService` for its own `/{studentId}/teachers/...`
  sub-resource endpoints rather than touching the join table directly. Keep this direction when touching relation
  logic — don't add join-table queries to the student side.

### Errors

`GlobalExceptionHandler` (`@RestControllerAdvice`) is the single place mapping exceptions to RFC 7807 `ProblemDetail`
responses: `ResourceNotFoundException` → 404, `ResourceAlreadyExistsException` → 409,
bean-validation/type-mismatch/unreadable-body → 400, catch-all → 500. Throw the domain exceptions from `exception/` (
constructed as `new ResourceNotFoundException("Entity", "field", value)`) from service methods rather than handling
not-found/conflict cases ad hoc in controllers.

### Filtering & pagination

List endpoints combine `Specification`s built from optional query params. Note the existing student/teacher/school
combinators use `Specification.where(...).or(...)` when chaining multiple optional filters (e.g.
`StudentServiceImpl.getStudents`) — be aware this means providing multiple filters is OR'd, not AND'd, which is likely
not the intended search semantics; check current behavior before assuming AND-filtering when adding new filtered fields.