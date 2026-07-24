package com.example.demo.student.service;

import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.persistence.Student;
import com.example.demo.student.persistence.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              SchoolRepository schoolRepository,
                              StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.studentMapper = studentMapper;
    }

    public List<StudentResponse> getStudents() {
        return studentMapper.toResponseList(studentRepository.findAll());
    }

    public StudentResponse addNewStudent(StudentRequest studentRequest) {
        studentRepository.findStudentByName(studentRequest.name())
                .ifPresent(s -> {
                    throw new IllegalStateException("Name taken.");
                });

        Student student = studentMapper.toEntity(studentRequest);
        student.setSchool(resolveSchool(studentRequest.schoolName()));

        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalStateException("Student " + studentId + " does not exist");
        }
        studentRepository.deleteById(studentId);
    }

    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("Student " + studentId + " does not exist"));

        if (studentRequest.name() != null && !studentRequest.name().isBlank()
                && !Objects.equals(student.getName(), studentRequest.name())) {
            student.setName(studentRequest.name());
        }

        if (studentRequest.dateOfBirth() != null
                && !Objects.equals(student.getDateOfBirth(), studentRequest.dateOfBirth())) {
            student.setDateOfBirth(studentRequest.dateOfBirth());
        }

        if (studentRequest.schoolName() != null && !studentRequest.schoolName().isBlank()) {
            student.setSchool(resolveSchool(studentRequest.schoolName()));
        }

        // no explicit save() needed — @Transactional + managed entity triggers dirty checking on commit
        return studentMapper.toResponse(student);
    }

    public StudentResponse searchStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("Student " + studentId + " does not exist"));
        return studentMapper.toResponse(student);
    }

    private School resolveSchool(String schoolName) {
        return schoolRepository.findSchoolBySchoolName(schoolName)
                .orElseThrow(() -> new IllegalStateException("School '" + schoolName + "' does not exist"));
    }
}