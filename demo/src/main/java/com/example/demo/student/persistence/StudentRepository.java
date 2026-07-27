package com.example.demo.student.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    //  SELECT * FROM STUDENT s WHERE s.name= ?1
    Optional<Student> findStudentByName(String name);

    boolean existsByEmail(String email);
}
