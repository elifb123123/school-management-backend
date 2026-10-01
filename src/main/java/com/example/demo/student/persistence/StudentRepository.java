package com.example.demo.student.persistence;

import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.user.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {


    @Query("SELECT t FROM Student s JOIN s.teachers t WHERE s.id = :studentId")
    List<Teacher> findTeachersByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s.school.id FROM Student s WHERE s.id = :studentId")
    Optional<Long> findSchoolIdById(@Param("studentId") Long studentId);

    @Query("SELECT s.user.email FROM Student s WHERE s.id = :id")
    Optional<String> findEmailById(@Param("id") Long id);

    Optional<Student> findByUser(User user);

    @Query("SELECT s.school.id FROM Student s WHERE s.user.email = :email")
    Optional<Long> findSchoolIdByUserEmail(@Param("email") String email);
}
