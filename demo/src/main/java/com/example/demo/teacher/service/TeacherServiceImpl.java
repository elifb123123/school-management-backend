package com.example.demo.teacher.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.mapper.TeacherMapper;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getTeachers(Pageable pageable) {
        List<Teacher> teachers = teacherRepository.findAll(pageable).getContent();
        return teacherMapper.toResponseList(teachers);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher ", "id", id));
        return teacherMapper.toResponse(teacher);
    }

    @Override
    public TeacherResponse createTeacher(TeacherRequest teacherRequest, Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School ", "id", schoolId));

        Teacher teacher = teacherMapper.toEntity(teacherRequest);
        teacher.setSchool(school);

        Teacher saved = teacherRepository.save(teacher);
        return teacherMapper.toResponse(saved);
    }

    @Override
    public TeacherResponse updateTeacher(TeacherRequest teacherRequest, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher ", "id", teacherId));

        teacher.setName(teacherRequest.name());

        // schoolName in the request may have changed; resolve and reassign the School
        School school = schoolRepository.findSchoolBySchoolName(teacherRequest.schoolName())
                .orElseThrow(() -> new ResourceNotFoundException("School ", "school name", teacherRequest.schoolName()));
        teacher.setSchool(school);

        Teacher updated = teacherRepository.save(teacher);
        return teacherMapper.toResponse(updated);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher ", "id", id);
        }
        teacherRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return teacherRepository.existsById(id);
    }

    @Override
    public boolean existsRelation(Long teacherId, Long studentId) {
        return teacherRepository.existsTeacherStudentRelation(teacherId, studentId);
    }

    @Override
    public void linkStudent(Long teacherId, Long studentId) {
        teacherRepository.insertTeacherStudentRelation(teacherId, studentId);
    }

    @Override
    public void unlinkStudent(Long teacherId, Long studentId) {
        teacherRepository.deleteTeacherStudentRelation(teacherId, studentId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsOfTeacher(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }

        return studentMapper.toResponseList(teacherRepository.findStudentsByTeacherId(teacherId));
    }
}