package com.example.demo.student;

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
    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping(path = "{studentId}")
    public Student searchStudent(@PathVariable Long studentId) {
        return studentService.searchStudent(studentId);
    }

    // TODO: RequestBody ??
    @PostMapping
    public ResponseEntity<Student> registerNewStudent(@RequestBody Student student) {
        studentService.addNewStudent(student);
        return ResponseEntity.status(201).body(student);
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
    }


    // TODO: PathVariable ve RequestParam farkı ???
    @PutMapping(path = "{studentId}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long studentId, @RequestParam(required = false) String name) {
        Student updated = studentService.updateStudent(studentId, name);
        return ResponseEntity.ok().body(updated);
    }

}
