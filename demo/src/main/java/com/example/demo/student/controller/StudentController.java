package com.example.demo.student.controller;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.service.StudentService;
import com.example.demo.teacher.dto.TeacherResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public Page<StudentResponse> getStudents(Pageable pageable,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(required = false) LocalDate birthDate) {

        return studentService.getStudents(name, email, birthDate, pageable);
    }

    @GetMapping(path = "/{studentId}")
    public StudentResponse getStudentById(@PathVariable Long studentId) {
        return studentService.searchStudent(studentId);
    }

    // TODO: RequestBody ??
    @PostMapping
    public ResponseEntity<StudentResponse> registerNewStudent(@RequestBody @Valid StudentRequest studentRequest) {
        StudentResponse created = studentService.addNewStudent(studentRequest);
        return ResponseEntity.status(201).body(created);
    }

    @DeleteMapping(path = "/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    // TODO: PathVariable ve RequestParam farkı ???
    @PutMapping(path = "/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long studentId, @RequestBody @Valid StudentRequest studentRequest) {
        StudentResponse updated = studentService.updateStudent(studentId, studentRequest);
        return ResponseEntity.ok().body(updated);
    }


    // --- ÖĞRETMEN - ÖĞRENCİ İLİŞKİ İŞLEMLERİ (Alt Kaynaklar) ---
    // Öğrenciye Öğretmen Ekle (Kayıt) @PostMapping("/{studentId}/teachers/{teacherId}")
    // Öğrencinin Öğretmen Kaydını Sil  @DeleteMapping("/{studentId}/teachers/{teacherId}")
    // Öğrencinin Ders Aldığı Öğretmenleri Getir  @GetMapping("/{studentId}/teachers")

    @PostMapping("/{studentId}/teachers/{teacherId}/link")
    public ResponseEntity<Void> linkTeacherToStudent(@PathVariable Long studentId, @PathVariable Long teacherId) {
        studentService.linkTeacherToStudent(studentId, teacherId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{studentId}/teachers/{teacherId}/unlink")
    public ResponseEntity<Void> unlinkTeacherFromStudent(@PathVariable Long studentId, @PathVariable Long teacherId) {
        studentService.unlinkTeacherFromStudent(studentId, teacherId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/teachers")
    public List<TeacherResponse> getTeachersOfStudent(@PathVariable Long studentId) {
        return studentService.getTeachersOfStudent(studentId);
    }

}


