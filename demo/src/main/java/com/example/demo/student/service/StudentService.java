package com.example.demo.student.service;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StudentService {

    List<StudentResponse> getStudents(String name, String email, LocalDate birthDate, Pageable pageable);

    StudentResponse addNewStudent(StudentRequest studentRequest);

    void deleteStudent(Long studentId);

    StudentResponse updateStudent(Long studentId, StudentRequest studentRequest);

    StudentResponse searchStudent(Long studentId);

    void addTeacherToStudent(Long studentId, Long teacherId);

    void deleteTeacherFromStudent(Long studentId, Long teacherId);

    List<TeacherResponse> getTeachersOfStudent(Long studentId);

}
