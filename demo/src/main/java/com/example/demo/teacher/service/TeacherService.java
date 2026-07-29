package com.example.demo.teacher.service;

import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TeacherService {

    // Tüm öğretmenleri listele (Zaten var)
    List<TeacherResponse> getTeachers();

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

    void linkStudent(Long teacherId, Long studentId);

    void unlinkStudent(Long teacherId, Long studentId);

    List<TeacherResponse> getTeachersByStudentId(Long studentId);

//    List<StudentResponse> getStudentsOfTeacher(Long teacherId);

}
