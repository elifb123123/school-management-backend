package com.example.demo.student;

import java.util.List;

public interface StudentService {

    List<Student> getStudents();

    void addNewStudent(Student student);

    void deleteStudent(Long studentId);

    Student updateStudent(Long studentId, String name);

    Student searchStudent(Long studentId);
}
