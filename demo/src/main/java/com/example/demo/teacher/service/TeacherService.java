package com.example.demo.teacher.service;

import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {


    // Tüm öğretmenleri listele
    Page<TeacherResponse> getTeachers(String name, Pageable pageable);

    // ID'ye göre tek bir öğretmen getir
    TeacherResponse getTeacherById(Long id);

    // Yeni öğretmen kaydet

    TeacherResponse createTeacher(TeacherRequest teacherRequest, Long schoolId);

    // Var olan öğretmeni güncelle
    TeacherResponse updateTeacher(TeacherRequest teacherRequest, Long TeacherId);

    // ID'ye göre öğretmen sil
    void deleteTeacher(Long id);

    boolean existsById(Long id);

    boolean existsRelation(Long teacherId, Long studentId);

    // Öğretmenle öğrenciyi ilişkilendir
    void linkStudent(Long teacherId, Long studentId);

    // Öğretmenle öğrenci ilişkisini kaldır
    void unlinkStudent(Long teacherId, Long studentId);

    //Belirli bir öğretmene ait öğrencileri getir
    List<StudentResponse> getStudentsOfTeacher(Long teacherId);

}
