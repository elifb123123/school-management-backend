# API Documentation

Base URL: `http://localhost:8080` (no context path, no auth, no CORS configured)

All list endpoints accept standard Spring pagination params: `page` (0-indexed), `size`, and repeatable
`sort=field,asc|desc`.

Errors are returned as RFC 7807 `ProblemDetail` JSON:

| Status | Trigger                          | Body shape                                                                       |
|--------|----------------------------------|----------------------------------------------------------------------------------|
| 400    | `@Valid` failure on request body | `{ title, status, detail, errors: { field: message } }`                          |
| 400    | Path/query param type mismatch   | `{ title, status, detail }`                                                      |
| 400    | Malformed JSON body              | `{ title, status, detail }`                                                      |
| 404    | `ResourceNotFoundException`      | `{ title, status, detail: "<Resource> bulunamadı, <field>: <value>" }`           |
| 409    | `ResourceAlreadyExistsException` | `{ title, status, detail: "<Resource> with <field> = <value> already exists." }` |
| 500    | Unhandled exception              | `{ title, status, detail }`                                                      |

---

## Schools — `/api/school`

| Method | Path                               | Request body    | Success | Response body           |
|--------|-------------------------------------|-----------------|---------|-------------------------|
| GET    | `/api/school?name=`                | —               | 200     | `Page<SchoolResponse>`  |
| GET    | `/api/school/{schoolId}`           | —               | 200     | `SchoolResponse`        |
| POST   | `/api/school`                      | `SchoolRequest` | 201     | `SchoolResponse`        |
| PUT    | `/api/school/{schoolId}`           | `SchoolRequest` | 200     | `SchoolResponse`        |
| DELETE | `/api/school/{schoolId}`           | —               | 204     | —                       |
| GET    | `/api/school/{schoolId}/students`  | —               | 200     | `Page<StudentResponse>` |
| GET    | `/api/school/{schoolId}/teachers`  | —               | 200     | `Page<TeacherResponse>` |

**`SchoolRequest`**

```json
{
  "schoolName": "string (required, not blank)"
}
```

**`SchoolResponse`**

```json
{
  "id": 1,
  "schoolName": "string"
}
```

---

## Teachers — `/api/teacher`

| Method | Path                                                   | Request body     | Success | Response body                       |
|--------|---------------------------------------------------------|------------------|---------|-------------------------------------|
| GET    | `/api/teacher?name=`                                   | —                | 200     | `Page<TeacherResponse>`             |
| GET    | `/api/teacher/{id}`                                    | —                | 200     | `TeacherResponse`                   |
| POST   | `/api/teacher`                                         | `TeacherRequest` | 201     | `TeacherResponse`                   |
| PUT    | `/api/teacher/{id}`                                    | `TeacherRequest` | 200     | `TeacherResponse`                   |
| DELETE | `/api/teacher/{id}`                                    | —                | 204     | —                                   |
| GET    | `/api/teacher/{id}/students`                           | —                | 200     | `List<StudentResponse>` (not paged) |
| POST   | `/api/teacher/{teacherId}/students/{studentId}/link`   | —                | 201     | — (empty)                           |
| DELETE | `/api/teacher/{teacherId}/students/{studentId}/unlink` | —                | 204     | —                                   |

**`TeacherRequest`**

```json
{
  "name": "string (required, not blank)",
  "schoolId": 1
}
```

**`TeacherResponse`**

```json
{
  "id": 1,
  "name": "string",
  "schoolName": "string"
}
```

---

## Students — `/api/student`

| Method | Path                                            | Request body     | Success | Response body                       |
|--------|--------------------------------------------------|------------------|---------|-------------------------------------|
| GET    | `/api/student?name=&email=&birthDate=`          | —                | 200     | `Page<StudentResponse>`             |
| GET    | `/api/student/{studentId}`                      | —                | 200     | `StudentResponse`                   |
| POST   | `/api/student`                                  | `StudentRequest` | 201     | `StudentResponse`                   |
| PUT    | `/api/student/{studentId}`                      | `StudentRequest` | 200     | `StudentResponse`                   |
| DELETE | `/api/student/{studentId}`                      | —                | 204     | —                                   |
| GET    | `/api/student/{studentId}/teachers`             | —                | 200     | `List<TeacherResponse>` (not paged) |
| POST   | `/api/student/{studentId}/teachers/{teacherId}` | —                | 204     | —                                   |
| DELETE | `/api/student/{studentId}/teachers/{teacherId}` | —                | 204     | —                                   |

`name`, `email`, and `birthDate` filters on the list endpoint are combined with AND.

**`StudentRequest`**

```json
{
  "name": "string (required, 3-20 chars)",
  "email": "string (required, valid email)",
  "dateOfBirth": "2003-03-05 (required, ISO date)",
  "schoolId": 1
}
```

**`StudentResponse`**

```json
{
  "id": 1,
  "name": "string",
  "email": "string",
  "dateOfBirth": "2003-03-05",
  "age": 23,
  "schoolName": "string"
}
```

---

## Known inconsistencies to be aware of

- **Student↔teacher relation ownership**: the `student_teacher` join table is owned by `Teacher`; both
  `/api/teacher/.../students/{studentId}/link|unlink` and `/api/student/.../teachers/{teacherId}` manage the same
  underlying relation from either side.
- **Link/unlink status codes differ by side**: the teacher-side link endpoint returns `201 Created`, while the
  student-side link endpoint returns `204 No Content` for the same underlying relation change.
- Sub-resource "list teachers of student" / "list students of teacher" endpoints are **not paginated**, unlike the
  top-level list endpoints.