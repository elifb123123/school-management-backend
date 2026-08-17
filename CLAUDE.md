# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Spring Boot 4.1.0 (Java 21) learning/demo REST API modeling Schools, Teachers, Students, and the `User` accounts
(with `PRINCIPAL`/`TEACHER`/`STUDENT` roles) that own them. Note: many source comments and validation messages are in
Turkish, and several files contain `// TODO:` learning-notes left by the author (e.g. "DEPENDENCY INJECTION NEDİR?")
— these are intentional study notes, not stale cruft to delete.

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
schema is dropped and recreated on every application start**, and a running Postgres instance on localhost is
required. H2 and `spring-boot-h2console` are on the classpath (runtime scope) but Postgres is the active
`spring.datasource.url`, so switch that property if you want to run against H2 instead.

`DataConfig` (`src/main/java/com/example/demo/config/DataConfig.java`) seeds two principals (each with their own
school: `principal1@gmail.com` → School 1, `principal2@gmail.com` → School 2), two students (`ayse`, `alex`, both at
School 1), and two teachers (`john.smith` at School 1, `maria.garcia` at School 2) via a `CommandLineRunner` if the
tables are empty on startup — useful reference data when testing endpoints manually (e.g. via
`generated-requests.http`). Since every `School`/`Teacher`/`Student` must own a `User` account (see below), the
seeder creates all of them through `UserService.registerPrincipal`/`registerTeacher`/`registerStudent` rather than
constructing entities directly. All seeded accounts use the password `"password"` and **log in with their email**,
not a username (see Security below) — e.g. `principal1@gmail.com` / `password`.

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
   repository lookup (e.g. resolving a `School` by id) are `@Mapping(target = ..., ignore = true)` and filled in by
   the service layer instead
6. `service/EntityService.java` (interface) + `service/EntityServiceImpl.java` (`@Service @Transactional`, constructor
   injection, `@Slf4j` logging on every mutation)
7. `controller/EntityController.java` — thin `@RestController`, delegates directly to the service, returns DTOs/
   `Page<DTO>` (list endpoints take `Pageable` plus optional `@RequestParam` filters)

A convention worth knowing when writing `update*`/read methods in any `*ServiceImpl`: an entity fetched via
`repository.findById(id)` inside a `@Transactional` method is a Hibernate-managed object — mutating it directly
(e.g. via a MapStruct `updateXFromRequest(request, @MappingTarget entity)` call) is enough, **no explicit
`repository.save(...)` is needed**, dirty-checking flushes the change automatically at commit. Pure-read methods
(`getById`, list, sub-resource GETs) should be annotated `@Transactional(readOnly = true)` instead of inheriting the
class-level read-write `@Transactional` — besides the minor performance win (skips dirty-checking entirely), it
makes Hibernate silently ignore any accidental entity mutation inside a method that's supposed to be read-only,
rather than quietly flushing it.

### Identity vs. domain profile (`user`, `security`)

`User` (`user/persistence/User.java`) holds login identity only — `email` (unique, used to log in), `password`
(BCrypt-hashed), a display `name` (**not** unique — real people share names, and it's shown in API responses but
never used to authenticate), and a `Role` enum (`PRINCIPAL`, `TEACHER`, `STUDENT`). `School`, `Teacher`, and `Student`
hold **only** their own domain-specific fields (`schoolName`/`address` for School, `branch`/`school` for Teacher,
`dateOfBirth`/`school` for Student) — they do **not** duplicate `name`/`email`; those are read through a mandatory,
cascading `@OneToOne` link to `User` (`School.user` / `Teacher.user` / `Student.user`, `nullable = false`,
`cascade = CascadeType.ALL`, `orphanRemoval = true`). Response DTOs (`TeacherResponse`/`StudentResponse`) still
expose `name`/`email` in their JSON shape — the mappers populate them via `@Mapping(source = "user.name", ...)` /
`@Mapping(source = "user.email", ...)`, not from a field on the entity itself.

Why the split: keeping `name`/`email` only on `User` means there's exactly one place they can get out of sync — a
`PUT` on a `Teacher`/`Student`/`School` can't silently diverge from the linked account's identity, because there's
nowhere else for that data to live.

There is deliberately **no standalone "create a bare School/Teacher/Student" endpoint** — the old `POST /api/school`
and `POST /api/teacher` were removed. The only way to create any of the three is through registration (below), which
guarantees every one has a linked `User` and prevents "orphan" domain records. `School.students`/`School.teachers`
also cascade (`CascadeType.ALL, orphanRemoval = true`) — deleting a `School` deletes every `Student`/`Teacher` at
that school, and deleting those in turn deletes their `User` accounts. This is intentional (a school going away
should take its accounts with it) and irreversible — there's no soft-delete step.

### Registration flow

`POST /api/register/{principal,teacher,student}` (`user/controller/UserController.java`) are the only entry points
that create `User`s (and, for teacher/student, the linked domain entity — for principal, a brand-new `School`)
together, atomically:

- `UserServiceImpl.register{Principal,Teacher,Student}` builds and saves the `User` first (setting the appropriate
  `Role`, rejecting duplicate emails via `userRepository.existsByEmail(...)` → `ResourceAlreadyExistsException`),
  **then** calls `SchoolService.registerSchool` / `TeacherService.registerTeacher` / `StudentService.registerStudent`
  with the already-persisted `User` — never the other way around, and never before the `User` has an id. The whole
  method is `@Transactional` so a failure on the domain-entity side (e.g. bad `schoolId`) rolls back the `User`
  insert too.
- `SchoolService`/`TeacherService`/`StudentService` own their own entity-construction logic (resolving `School` by
  id, setting the `user` association) — `UserServiceImpl` never builds a `School`/`Teacher`/`Student` itself, it only
  orchestrates. This orchestration logic intentionally lives inside `UserServiceImpl` rather than a separate
  `RegistrationService` — revisit that if a fourth role with its own linked entity shows up, or `UserServiceImpl`
  needs to inject a fourth cross-domain service; until then the coordination surface is small enough not to be worth
  a dedicated layer.
- Composite request DTOs live in `user/dto/` and wrap the existing per-domain request records instead of redeclaring
  fields: `PrincipalRegistrationRequest(UserRequest, SchoolRequest)`, `TeacherRegistrationRequest(UserRequest,
  TeacherRequest)`, `StudentRegistrationRequest(UserRequest, StudentRequest)`.
- `registerTeacher`/`registerStudent` are `@PreAuthorize`-protected directly on the `UserServiceImpl` methods (e.g.
  `@PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #teacherRegistrationRequest.teacherRequest.schoolId)")`),
  reaching into the composite DTO's nested `schoolId` via SpEL — see the `@PreAuthorize`/`*Security` bean pattern
  under Security below for how this and every other ownership check in the app works. This replaced an earlier
  hand-written `ensureSchoolMatchesPrincipal` helper that did the same lookup manually; `UserController` no longer
  needs an injected `Authentication` parameter because `authentication.name` is read directly inside the SpEL
  expression instead. This is a **business-rule check layered on top of** the role-based `SecurityConfig` rules
  below, not a replacement for them — `hasRole("PRINCIPAL")` only proves "some principal," this proves "the
  principal who owns *this* school."

### Security (`security/`, plus a `security/` subpackage per domain)

`SecurityConfig` and `UserDetailServiceImpl` live in the top-level `security/`, not `config/` — they're generic
framework-integration code. Per-domain **authorization logic** (below) lives instead in each domain's own
`security/` subpackage (`school/security/SchoolSecurity.java`, `teacher/security/TeacherSecurity.java`,
`student/security/StudentSecurity.java`) — the top-level `security/` package is reserved for code with nothing
domain-specific in it (login mechanics, the filter chain), not for "who can touch this School/Teacher/Student."

**Login is email-based, not username-based.** `UserDetailServiceImpl.loadUserByUsername` looks users up via
`UserRepository.findByEmail(...)` (despite the method being named `loadUserByUsername` — that name is fixed by the
`UserDetailsService` interface contract, the parameter itself is just treated as an email) and builds the Spring
Security `UserDetails` with `.username(user.getEmail())` — this second part matters: it's what makes
`Authentication.getName()` return the email later (used by every ownership check below), so if login is ever
changed to key off something else, that builder call has to change too, not just the repository lookup. There's no
fallback `spring.security.user.*` account. Authorities are built as `"ROLE_" + role.name()` (never `.toString()` —
`name()` is `final` on `Enum`, `toString()` is overridable and would silently break the authority string if someone
customized it later).

**URL-level rules** (`SecurityConfig.filterChain`): `/api/register/principal` is `permitAll()` (bootstrapping —
there would otherwise be no way to create the first account), `/api/register/teacher` and `/api/register/student`
require `hasRole("PRINCIPAL")`, `GET /api/school`/`/api/teacher`/`/api/student` (the bare collection endpoints —
exact-path match, does **not** cover `/api/school/{id}` or any sub-resource) require `hasRole("ADMIN")` — reserved
for a future admin role/UI that doesn't exist yet (`Role.ADMIN` is in the enum but not currently assignable to any
real account), everything else requires `authenticated()`.

**`@PreAuthorize` + per-domain `*Security` bean pattern.** `SecurityConfig` carries `@EnableMethodSecurity` — without
it every `@PreAuthorize` annotation below would be silently ignored, and every non-register endpoint would just fall
back to the coarser `authenticated()` URL rule above. This is how every School/Teacher/Student CRUD method
(create/update/delete/link/unlink, plus by-id and sub-resource GETs) enforces "who can touch *this specific row*":

- `SchoolSecurity.isPrincipalOf(String principalEmail, Long schoolId)` is the **root** check every other domain's
  check routes through: looks up the `User` by email, returns `true` immediately if `role == ADMIN` (the one place
  the ADMIN bypass is implemented — new checks never need their own ADMIN branch, they just call into this),
  otherwise resolves the caller's own `School` via `SchoolRepository.findByUser` and compares its id to `schoolId`.
- `TeacherSecurity`/`StudentSecurity` each expose `findSchoolId(Long id)` and `isSelf(String requesterEmail, Long id)`.
  Both delegate to a **repository projection query** (`TeacherRepository.findSchoolIdById`/`findEmailById`, JPQL
  `SELECT t.school.id FROM Teacher t WHERE t.id = :id` / `SELECT t.user.email FROM Teacher t WHERE t.id = :id`) —
  deliberately **not** `teacherRepository.findById(id)` plus navigating `.getSchool().getId()`/`.getUser().getEmail()`
  in Java, because `Teacher.school` is `@ManyToOne` (default `EAGER`), so `findById` would hydrate the entire
  `Teacher` row plus a joined `School` row just to read one FK/email column — wasteful for a check that runs on
  every write and every by-id/sub-resource read. Same pattern for `StudentSecurity`/`StudentRepository`.
- These bean methods must **return `false`/`null`, never throw**, when the id isn't found — throwing inside a
  `@PreAuthorize` SpEL bean-method call gets wrapped by SpEL's method-invocation machinery into a
  `SpelEvaluationException`, which `GlobalExceptionHandler`'s `ResourceNotFoundException` handler won't recognize,
  so it falls through to the generic 500 catch-all instead of a clean 404. Net effect (accepted, consistent behavior
  across the app): calling a write/read-by-id endpoint with a nonexistent id returns **403**, not 404 — the
  `@PreAuthorize` check runs and fails before the method body's own `existsById`/`orElseThrow` check is ever reached.
- Composition in `@PreAuthorize` strings nests bean calls as SpEL arguments, e.g.
  `@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))`. `||` combines
  "principal of the owning school" with "the resource's own owner" for read access (e.g.
  `getTeacherById`/`getStudentsOfTeacher`: `isPrincipalOf(...) || isSelf(...)`). `&&` combines two *different*
  resources' ownership for link/unlink operations (e.g. `linkTeacherToStudent`/`TeacherServiceImpl.linkStudent`: the
  caller must own *both* the teacher's school and the student's school) — since a principal only ever owns one
  `School` (`SchoolRepository.findByUser` returns a singular `Optional`), this `&&` also implicitly forces
  teacher+student to be at the same school.
- Resulting access model: **PRINCIPAL** gets full read/write on everything belonging to their own school (school
  info, its teachers, its students, their links) via `isPrincipalOf`. **TEACHER**/**STUDENT** get read-only access
  to their own profile and their own linked students/teachers via `isSelf` — they have **zero** write endpoints
  (`updateTeacher`/`deleteTeacher`/`linkStudent`/etc. only check `isPrincipalOf`, no `isSelf` branch, so a teacher
  editing their own row correctly still gets 403). **ADMIN** bypasses every check via `SchoolSecurity.isPrincipalOf`'s
  role check, plus the URL-level collection-endpoint rule above.
- Why `SchoolService.registerSchool` is the one CRUD method that is **not** `@PreAuthorize`-protected: it's called
  from `UserServiceImpl.registerPrincipal`, which sits behind `permitAll()` — there is no logged-in caller yet (the
  `User` being registered is brand new and has never authenticated, so `authentication` for that request is
  anonymous, not "the new principal"). Assigning `Role.PRINCIPAL` to the in-memory `User` object is just setting a
  field on freshly-constructed data; it does not make `authentication` refer to that user. `registerSchool` instead
  does a plain `if (user.getRole() != PRINCIPAL && user.getRole() != ADMIN) throw new AccessDeniedException(...)`
  check directly on the passed-in `User` parameter. Note this specific check *could* technically be written as
  `@PreAuthorize("#user.role.name() == 'PRINCIPAL' or ...")` — SpEL can read any method parameter, not only
  `authentication` — but it's input validation on the method's own argument, not "who is allowed to call me," so it
  stays a plain `if` rather than repurposing a caller-identity annotation for it.
- `DataConfig`'s `CommandLineRunner` runs with **no** `Authentication` in the `SecurityContextHolder` at all (it's
  not an HTTP request), so calling *any* `@PreAuthorize`-protected service method from the seeder — this now
  includes `TeacherService.linkStudent`, not just `UserService.register{Teacher,Student}` — needs the
  `actAsPrincipal(String principalEmail)` helper called first (injects a fake `UsernamePasswordAuthenticationToken`
  with `ROLE_PRINCIPAL`) and `SecurityContextHolder.clearContext()` after. Forgetting this around a newly-protected
  seed call fails app startup with an `AuthenticationCredentialsNotFoundException` wrapped inside a
  `SpelEvaluationException` — check this first if `DataConfig` ever fails on a fresh boot right after adding a new
  `@PreAuthorize`.

### Cross-domain relationships

- `Student.school` / `Teacher.school` are `@ManyToOne` onto `School`; `SchoolRepository` exposes `findStudentsById`/
  `findTeachersById` as paged JPQL queries for the school's sub-resource endpoints.
- The Student↔Teacher relationship is `@ManyToMany`, owned by `Teacher` (`Teacher.students`, join table
  `student_teacher`); `Student.teachers` is the inverse (`mappedBy`) side. Because of this, **all relation mutations
  (link/unlink/existence checks) live in `TeacherRepository`/`TeacherService`** (native SQL queries against
  `student_teacher`), and `StudentServiceImpl` delegates to `TeacherService` for its own `/{studentId}/teachers/...`
  sub-resource endpoints rather than touching the join table directly. Keep this direction when touching relation
  logic — don't add join-table queries to the student side.
- `User` does **not** carry inverse `@OneToOne(mappedBy = "user")` navigation fields back to `School`/`Teacher`/
  `Student` — this was tried and removed: nothing in the codebase used them, `@OneToOne` defaults to `EAGER`, and it
  would have added an unconditional extra query to every `User` load (including every basic-auth check in
  `UserDetailServiceImpl.loadUserByUsername`). Add them back as `FetchType.LAZY`, with both sides of the association
  kept in sync on write, only when something actually needs `user.getSchool()`/`getTeacher()`/`getStudent()`
  navigation. (`SchoolService.getSchoolByPrincipal` currently does this lookup the other way — `SchoolRepository.
  findByUser(user)` — precisely to avoid needing that inverse field.)

### Errors

`GlobalExceptionHandler` (`@RestControllerAdvice`) is the single place mapping exceptions to RFC 7807 `ProblemDetail`
responses: `ResourceNotFoundException` → 404, `ResourceAlreadyExistsException` → 409, `AccessDeniedException` → 403,
bean-validation/type-mismatch/unreadable-body → 400, catch-all → 500. Throw the domain exceptions from `exception/`
(constructed as `new ResourceNotFoundException("Entity", "field", value)`) from service methods rather than handling
not-found/conflict/forbidden cases ad hoc in controllers.

Two gotchas specific to this handler:

- A raw DB-level unique-constraint violation (e.g. inserting a `User` without an `existsByEmail` pre-check) surfaces
  as an unhandled `DataIntegrityViolationException` → generic 500 via the catch-all, not a clean 409 — always add the
  proactive `existsBy...` check + `ResourceAlreadyExistsException` in the service rather than relying on the DB
  constraint alone to produce a good error response.
- The broad `@ExceptionHandler(Exception.class)` catch-all pre-empts Spring's/Spring Security's own default handling
  for framework exceptions that would otherwise resolve to a more specific status — e.g. `AccessDeniedException`
  normally gets turned into 403 automatically by `ExceptionTranslationFilter`, but never gets the chance here because
  the catch-all (which runs inside `DispatcherServlet`, before the exception could propagate out to that filter)
  handles it first, as a 500. This is why `AccessDeniedException` has its own explicit handler above. The same is
  still true of `HttpRequestMethodNotSupportedException` (e.g. `POST /api/school`, which has no POST handler) — it
  currently falls through to the catch-all and returns 500 instead of 405; this was deliberately left unfixed for
  now (low priority, easy to add a handler for later using the same pattern as `AccessDeniedException` if needed).

### CORS

`CorsConfig` (`config/CorsConfig.java`) allows `http://localhost:*` origins (any localhost port) with
GET/POST/PUT/DELETE/PATCH/OPTIONS and all headers, scoped to `/api/**`. Update this if a frontend is served from a
non-localhost origin.

### Filtering & pagination

List endpoints combine `Specification`s built from optional query params. Note the existing student/teacher/school
combinators use `Specification.where(...).or(...)` when chaining multiple optional filters (e.g.
`StudentServiceImpl.getStudents`) — be aware this means providing multiple filters is OR'd, not AND'd, which is
likely not the intended search semantics; check current behavior before assuming AND-filtering when adding new
filtered fields.
