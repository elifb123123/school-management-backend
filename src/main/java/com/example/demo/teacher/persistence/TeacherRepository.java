package com.example.demo.teacher.persistence;

import com.example.demo.student.persistence.Student;
import com.example.demo.user.persistence.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO student_teacher (teacher_id, student_id) VALUES (:teacherId, :studentId)", nativeQuery = true)
    void insertTeacherStudentRelation(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student_teacher WHERE teacher_id = :teacherId AND student_id = :studentId", nativeQuery = true)
    void deleteTeacherStudentRelation(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM student_teacher WHERE teacher_id = :teacherId AND student_id = :studentId)", nativeQuery = true)
    boolean existsTeacherStudentRelation(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    @Query("SELECT s FROM Teacher t JOIN t.students s WHERE t.id = :teacherId")
    List<Student> findStudentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT t.school.id FROM Teacher t WHERE t.id = :teacherId")
    Optional<Long> findSchoolIdById(@Param("teacherId") Long teacherId);

    @Query("SELECT t.user.email FROM Teacher t WHERE t.id = :teacherId")
    Optional<String> findEmailById(@Param("teacherId") Long teacherId);

    Optional<Teacher> findByUser(User user);

    // TeacherRepository.java
    @Query("SELECT t.school.id FROM Teacher t WHERE t.user.email = :email")
    Optional<Long> findSchoolIdByUserEmail(@Param("email") String email);
}
