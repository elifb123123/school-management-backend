package com.example.demo.school.persistence;

import com.example.demo.student.persistence.Student;
import com.example.demo.teacher.persistence.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findSchoolBySchoolName(String schoolName);

    boolean existsBySchoolName(String schoolName);

    @Query("SELECT s.students FROM School s WHERE s.id = :schoolId")
    List<Student> findStudentsById(@Param("schoolId") Long schoolId);

    @Query("SELECT s.teachers FROM School s WHERE s.id = :schoolId")
    List<Teacher> findTeachersById(@Param("schoolId") Long schoolId);
}
