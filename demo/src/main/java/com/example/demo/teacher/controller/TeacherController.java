package com.example.demo.teacher.controller;

import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // GET /api/teachers?page=0&size=10&sort=id,desc&sort=name,asc
    @GetMapping
    public Page<TeacherResponse> getTeachers(Pageable pageable,
                                             @RequestParam(required = false) String name) {

        // TODO: SERVCE'E TAŞI ...

        return teacherService.getTeachers(name, pageable);
    }

    // GET /api/teachers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long id) {
        TeacherResponse teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    // POST /api/{schoolID}/teachers
    @PostMapping("/{schoolId}/teachers")
    public ResponseEntity<TeacherResponse> createTeacher(@PathVariable Long schoolId, @RequestBody @Valid TeacherRequest teacherRequest) {
        //Don't want orphan teachers
        TeacherResponse created = teacherService.createTeacher(teacherRequest, schoolId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/teachers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable Long id, @RequestBody @Valid TeacherRequest teacherRequest) {
        TeacherResponse updated = teacherService.updateTeacher(teacherRequest, id);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/teachers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/students")
    public ResponseEntity<List<StudentResponse>> getStudentsOfTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getStudentsOfTeacher(id));
    }

    // POST /api/teachers/1/students/5 -> Öğretmene öğrenci bağlar
    @PostMapping("/{teacherId}/students/{studentId}/link")
    public ResponseEntity<Void> linkStudent(
            @PathVariable Long teacherId,
            @PathVariable Long studentId) {

        teacherService.linkStudent(teacherId, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /api/teachers/1/students/5 -> Öğretmenden öğrenciyi koparır
    @DeleteMapping("/{teacherId}/students/{studentId}/unlink")
    public ResponseEntity<Void> unlinkStudent(
            @PathVariable Long teacherId,
            @PathVariable Long studentId) {

        teacherService.unlinkStudent(teacherId, studentId);
        return ResponseEntity.noContent().build();
    }
}
