package com.example.demo.school.persistence;

import com.example.demo.student.persistence.Student;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.user.persistence.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long>, JpaSpecificationExecutor<School> {
    Optional<School> findSchoolBySchoolName(String schoolName);

    boolean existsBySchoolName(String schoolName);

    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId")
    Page<Student> findStudentsById(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT s.teachers FROM School s WHERE s.id = :schoolId")
    Page<Teacher> findTeachersById(@Param("schoolId") Long schoolId, Pageable pageable);

    Optional<School> findByUser(User user);
}
