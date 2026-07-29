package com.example.demo.teacher.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // allow finding teachers by student id without loading student entity in service layer
    List<Teacher> findAllByStudentsId(Long studentId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO course (teacher_id, student_id) VALUES (:teacherId, :studentId)", nativeQuery = true)
    void insertTeacherStudentRelation(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM course WHERE teacher_id = :teacherId AND student_id = :studentId", nativeQuery = true)
    void deleteTeacherStudentRelation(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM course WHERE teacher_id = :teacherId AND student_id = :studentId)", nativeQuery = true)
    boolean existsTeacherStudentRelation(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

}
