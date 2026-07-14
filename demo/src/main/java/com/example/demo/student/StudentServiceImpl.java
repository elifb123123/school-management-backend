package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();

    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository.findStudentByName(student.getName());
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("Name taken.");
        }
        studentRepository.save(student);
        System.out.println(student);

    }

    public void deleteStudent(Long studentId) {
        System.out.println("Servise gelen id: " + studentId);

        boolean exists = studentRepository.existsById(studentId);

        if (!exists) {
            throw new IllegalStateException(" student " + studentId + "does not exist");
        }
        studentRepository.deleteById(studentId);
    }

    public void updateStudent(Long studentId, String name) {

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException(" student " + studentId + "does not exist"));

        if (name != null && name.length() > 0 &&
                !Objects.equals(student.getName(), name)) {
            student.setName(name);
        }
    }

}
