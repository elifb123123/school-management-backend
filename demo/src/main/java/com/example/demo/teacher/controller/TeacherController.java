package com.example.demo.teacher.controller;

import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // GET /api/teachers
    @GetMapping
    public List<Teacher> getTeachers() {
        return teacherService.getTeachers();
    }

    // GET /api/teachers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    // POST /api/{schoolID}/teachers
    @PostMapping("/{schoolId}/teachers")
    public ResponseEntity<Teacher> createTeacher(@PathVariable Long schoolId, @RequestBody Teacher teacher) {
        //Don't want orphan teachers
        Teacher created = teacherService.createTeacher(teacher, schoolId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /api/teachers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
