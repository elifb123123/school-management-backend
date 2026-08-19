package com.example.demo.teacher.security;

import com.example.demo.teacher.persistence.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("teacherSecurity")
@RequiredArgsConstructor //constructor injection
public class TeacherSecurity {
    private final TeacherRepository teacherRepository;

    public Long findSchoolId(Long teacherId) {
        return teacherRepository.findSchoolIdById(teacherId).orElse(null);
    }


    public boolean isSelf(String requesterEmail, Long teacherId) {
        return teacherRepository.findEmailById(teacherId).map(email -> email.equals(requesterEmail)).orElse(false);
    }

    public boolean isAtSchool(String requesterEmail, Long schoolId) {
        return teacherRepository.findSchoolIdByUserEmail(requesterEmail)
                .map(schoolId::equals)
                .orElse(false);
    }
}
