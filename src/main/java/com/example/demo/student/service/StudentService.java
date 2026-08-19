package com.example.demo.student.service;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.user.persistence.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StudentService {

    Page<StudentResponse> getStudents(String name, String email, LocalDate birthDate, Pageable pageable);

    StudentResponse registerStudent(StudentRequest studentRequest, User user);

    void deleteStudent(Long studentId);

    StudentResponse updateStudent(Long studentId, StudentRequest studentRequest);

    StudentResponse searchStudent(Long studentId);

    void linkTeacherToStudent(Long studentId, Long teacherId);

    void unlinkTeacherFromStudent(Long studentId, Long teacherId);

    List<TeacherResponse> getTeachersOfStudent(Long studentId);

    Long getStudentIdByUser(User user);

}
