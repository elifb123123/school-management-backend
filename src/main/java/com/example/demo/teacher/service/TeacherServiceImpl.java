package com.example.demo.teacher.service;

import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.persistence.Student;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.mapper.TeacherMapper;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import com.example.demo.teacher.persistence.specification.TeacherSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

    // todo: LOGGING....

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponse> getTeachers(String name, Pageable pageable) {
        Specification<Teacher> spec = Specification.where(TeacherSpecification.byName(name));
        Page<Teacher> teachers = teacherRepository.findAll(spec, pageable);
        log.info("Teachers retrieved: {}", teachers.getContent());
        return teachers.map(teacherMapper::toResponse);

    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher ", "id", id));
        log.info("Successfully fetched teacher by ID: {}", id);
        return teacherMapper.toResponse(teacher);
    }

    @Override
    public TeacherResponse createTeacher(TeacherRequest teacherRequest) {
        Long schoolId = teacherRequest.schoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School ", "id", schoolId));

        Teacher teacher = teacherMapper.toEntity(teacherRequest);
        teacher.setSchool(school);

        Teacher saved = teacherRepository.save(teacher);
        log.info("Created teacher: {}", saved.getName());
        return teacherMapper.toResponse(saved);
    }

    @Override
    public TeacherResponse updateTeacher(TeacherRequest teacherRequest, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher ", "id", teacherId));

        teacher.setName(teacherRequest.name());

        // schoolId in the request may have changed; resolve and reassign the School
        School school = schoolRepository.findById(teacherRequest.schoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School ", "school ID", teacherRequest.schoolId()));
        teacher.setSchool(school);

        Teacher updated = teacherRepository.save(teacher);
        log.info("Updated teacher: {}", updated.getName());
        return teacherMapper.toResponse(updated);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher ", "id", id);
        }
        teacherRepository.deleteById(id);
        log.info("Deleted teacher with ID: {}", id);
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
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }
        if (teacherRepository.existsTeacherStudentRelation(teacherId, studentId)) {
            throw new ResourceAlreadyExistsException("Student-Teacher relation", "studentId-teacherId", studentId + "-" + teacherId);
        } else teacherRepository.insertTeacherStudentRelation(teacherId, studentId);
        log.info("Linked student{} and teacher {}", studentId, teacherId);
    }

    // todo: parametrelerin if checkleri yapılmalı ...
    @Override
    public void unlinkStudent(Long teacherId, Long studentId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }
        if (!teacherRepository.existsTeacherStudentRelation(teacherId, studentId)) {
            throw new ResourceNotFoundException("Student-Teacher relation", "studentId-teacherId", studentId + "-" + teacherId);
        } else teacherRepository.deleteTeacherStudentRelation(teacherId, studentId);
        log.info("Unlinked student{} and teacher {}", studentId, teacherId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsOfTeacher(Long teacherId) {
        if (Objects.nonNull(teacherId) && !teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }
        List<Student> studentList = teacherRepository.findStudentsByTeacherId(teacherId);
        log.info("Fetched students for teacher with ID: {}", teacherId);
        return studentMapper.toResponseList(studentList);
    }
}