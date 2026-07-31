package com.example.demo.teacher.controller;

import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<TeacherResponse> getTeachers(@RequestParam(required = false, defaultValue = "10") int pageSize,
                                             @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                             @RequestParam(required = false, defaultValue = "id") String sortBy,
                                             @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                             @RequestParam(required = false) String name) {

        Sort sort = Sort.by(sortBy);
        if ("DESC".equalsIgnoreCase(sortDir)) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

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
    @PostMapping("/{teacherId}/students/{studentId}")
    public ResponseEntity<Void> addStudent(
            @PathVariable Long teacherId,
            @PathVariable Long studentId) {

        teacherService.linkStudent(teacherId, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /api/teachers/1/students/5 -> Öğretmenden öğrenciyi koparır
    @DeleteMapping("/{teacherId}/students/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long teacherId,
            @PathVariable Long studentId) {

        teacherService.unlinkStudent(teacherId, studentId);
        return ResponseEntity.noContent().build();
    }
}
