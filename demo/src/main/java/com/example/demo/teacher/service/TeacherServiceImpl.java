package com.example.demo.teacher.service;

import com.example.demo.school.persistence.School;
import com.example.demo.school.service.SchoolService;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private SchoolService schoolService;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository, SchoolService schoolService) {
        this.teacherRepository = teacherRepository;
        this.schoolService = schoolService;
    }

    @Override
    public List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Öğretmen bulunamadı! ID: " + id));
    }

    @Override
    public Teacher createTeacher(Teacher teacher, Long schoolId) {

        School school = schoolService.searchSchool(schoolId);
        teacher.setSchool(school);
        return teacherRepository.save(teacher);
    }


    @Override
    public void deleteTeacher(Long id) {
        // Silmeden önce var olup olmadığını kontrol ediyoruz
        Teacher teacher = getTeacherById(id);

        teacherRepository.delete(teacher);
    }
}