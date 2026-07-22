package com.example.demo.teacher.service;

import com.example.demo.teacher.persistence.Teacher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TeacherService {

    // Tüm öğretmenleri listele (Zaten var)
    List<Teacher> getTeachers();

    // ID'ye göre tek bir öğretmen getir
    Teacher getTeacherById(Long id);

    // Yeni öğretmen kaydet

    Teacher createTeacher(Teacher teacher, Long schoolId);

    // Var olan öğretmeni güncelle

    // ID'ye göre öğretmen sil
    void deleteTeacher(Long id);
}
