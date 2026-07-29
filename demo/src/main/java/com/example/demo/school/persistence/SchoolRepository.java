package com.example.demo.school.persistence;

import com.example.demo.student.persistence.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findSchoolBySchoolName(String schoolName);

    boolean existsBySchoolName(String schoolName);

    List<Student> findStudentsById(Long schoolId);
}
