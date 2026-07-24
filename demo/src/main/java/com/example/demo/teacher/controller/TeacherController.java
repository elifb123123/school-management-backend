package com.example.demo.teacher.controller;

import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.service.TeacherService;
import jakarta.validation.Valid;
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
    public List<TeacherResponse> getTeachers() {
        return teacherService.getTeachers();
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
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable Long id, @RequestBody TeacherRequest teacherRequest) {
        TeacherResponse updated = teacherService.updateTeacher(teacherRequest, id);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/teachers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
