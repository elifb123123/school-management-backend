package com.example.demo.student.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    //  SELECT * FROM STUDENT s WHERE s.name= ?1
    Optional<Student> findStudentByName(String name);

    boolean existsByEmail(String email);

    List<Student> findAllByNameContaining(String name);

    List<Student> findAllBySchool_SchoolId(Long schoolId);

    List<Student> findAllByAgeGreaterThan(Integer age);

    List<Student> findAllByTeachersId(Long teachersId);


}
