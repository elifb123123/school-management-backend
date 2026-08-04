package com.example.demo.teacher.persistence;

import com.example.demo.student.persistence.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
