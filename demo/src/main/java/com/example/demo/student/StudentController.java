package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {

    // TODO: DEPENDENCY INJECTION NEDİR NEDEN YAPILIR ? NASIL YAPILIR ? yapmayınca ne oluyor ?
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }


    @GetMapping
    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    // TODO: RequestBody ??
    @PostMapping
    public void registerNewStudent(@RequestBody Student student) {
        studentService.addNewStudent(student);
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
    }


    // TODO: PathVariable ve RequestParam farkı ???
    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable Long studentId, @RequestParam(required = false) String name) {
        studentService.updateStudent(studentId, name);
    }

}
