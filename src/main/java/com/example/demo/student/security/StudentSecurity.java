package com.example.demo.student.security;

import com.example.demo.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("studentSecurity")
@RequiredArgsConstructor
public class StudentSecurity {
    private final StudentRepository studentRepository;

    public Long findSchoolId(Long studentId) {
        return studentRepository.findSchoolIdById(studentId).orElse(null);
    }

    public boolean isSelf(String requestEmail, Long studentId) {
        return studentRepository.findEmailById(studentId).map(requestEmail::equals).orElse(false);
    }
}
