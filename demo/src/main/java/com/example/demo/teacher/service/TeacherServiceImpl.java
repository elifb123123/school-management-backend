package com.example.demo.teacher.service;

import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.mapper.TeacherMapper;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    private final TeacherMapper teacherMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teacherMapper.toResponseList(teachers);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
        return teacherMapper.toResponse(teacher);
    }

    @Override
    public TeacherResponse createTeacher(TeacherRequest teacherRequest, Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new EntityNotFoundException("School not found with id: " + schoolId));

        Teacher teacher = teacherMapper.toEntity(teacherRequest);
        teacher.setSchool(school);

        Teacher saved = teacherRepository.save(teacher);
        return teacherMapper.toResponse(saved);
    }

    @Override
    public TeacherResponse updateTeacher(TeacherRequest teacherRequest, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));

        teacher.setName(teacherRequest.name());

        // schoolName in the request may have changed; resolve and reassign the School
        School school = schoolRepository.findSchoolBySchoolName(teacherRequest.schoolName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "School not found with name: " + teacherRequest.schoolName()));
        teacher.setSchool(school);

        Teacher updated = teacherRepository.save(teacher);
        return teacherMapper.toResponse(updated);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }
}