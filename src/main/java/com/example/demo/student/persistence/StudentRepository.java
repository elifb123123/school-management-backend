package com.example.demo.student.persistence;

import com.example.demo.teacher.persistence.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {


    @Query("SELECT t FROM Student s JOIN s.teachers t WHERE s.id = :studentId")
    List<Teacher> findTeachersByStudentId(@Param("studentId") Long studentId);


}
