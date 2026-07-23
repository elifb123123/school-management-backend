package com.example.demo.student.controller;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/student")
public class StudentController {

    // TODO: DEPENDENCY INJECTION NEDİR NEDEN YAPILIR ? NASIL YAPILIR ? yapmayınca ne oluyor ?
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping
    public List<StudentResponse> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping(path = "/{studentId}")
    public StudentResponse searchStudent(@PathVariable Long studentId) {
        return studentService.searchStudent(studentId);
    }

    // TODO: RequestBody ??
    @PostMapping
    public ResponseEntity<StudentResponse> registerNewStudent(@RequestBody StudentRequest studentRequest) {
        StudentResponse created = studentService.addNewStudent(studentRequest);
        return ResponseEntity.status(201).body(created);
    }

    @DeleteMapping(path = "/{studentId}")
    public void deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
    }


    // TODO: PathVariable ve RequestParam farkı ???
    @PutMapping(path = "/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long studentId, @RequestBody StudentRequest studentRequest) {
        StudentResponse updated = studentService.updateStudent(studentId, studentRequest);
        return ResponseEntity.ok().body(updated);
    }

}
