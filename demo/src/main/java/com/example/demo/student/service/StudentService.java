package com.example.demo.student.service;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;

import java.util.List;

public interface StudentService {

    List<StudentResponse> getStudents();

    StudentResponse addNewStudent(StudentRequest studentRequest);

    void deleteStudent(Long studentId);

    StudentResponse updateStudent(Long studentId, StudentRequest studentRequest);

    StudentResponse searchStudent(Long studentId);
}
