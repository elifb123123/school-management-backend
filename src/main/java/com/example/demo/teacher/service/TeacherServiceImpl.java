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
import com.example.demo.teacher.persistence.Branch;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import com.example.demo.teacher.persistence.specification.TeacherSpecification;
import com.example.demo.user.persistence.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId)) " +
            "|| @teacherSecurity.isSelf(authentication.name, #teacherId)")
    public TeacherResponse getTeacherById(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher ", "id", teacherId));
        log.info("Successfully fetched teacher by ID: {}", teacherId);
        return teacherMapper.toResponse(teacher);
    }

    @Override
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #teacherRequest.schoolId())")
    public TeacherResponse registerTeacher(TeacherRequest teacherRequest, User user) {
        Long schoolId = teacherRequest.schoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School ", "id", schoolId));

        Teacher teacher = teacherMapper.toEntity(teacherRequest);
        teacher.setSchool(school);
        teacher.setUser(user);

        Teacher saved = teacherRepository.save(teacher);
        log.info("Registered teacher: {}", saved.getId());
        return teacherMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))")
    public TeacherResponse updateTeacher(TeacherRequest teacherRequest, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher ", "id", teacherId));

        teacherMapper.updateTeacherFromRequest(teacherRequest, teacher);
        //okul değiştirilemez.

        // gereksiz tekrar. hibernate zaten güncelliyor.
        // Teacher updated = teacherRepository.save(teacher);
        log.info("Updated teacher: {}", teacher.getId());
        return teacherMapper.toResponse(teacher);
    }

    @Override
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))")
    public void deleteTeacher(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher ", "id", teacherId);
        }
        teacherRepository.deleteById(teacherId);
        log.info("Deleted teacher with ID: {}", teacherId);
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
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))" +
            " && @schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId))")
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
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))" +
            " && @schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId))")
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
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId)) || " +
            "@teacherSecurity.isSelf(authentication.name, #teacherId)")
    public List<StudentResponse> getStudentsOfTeacher(Long teacherId) {
        if (Objects.nonNull(teacherId) && !teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }
        List<Student> studentList = teacherRepository.findStudentsByTeacherId(teacherId);
        log.info("Fetched students for teacher with ID: {}", teacherId);
        return studentMapper.toResponseList(studentList);
    }


    @Override
    public List<String> getAllBranches() {
        return Arrays.stream(Branch.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTeacherIdByUser(User user) {
        Teacher teacher = teacherRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "user", user.getId()));
        return teacher.getId();
    }
}