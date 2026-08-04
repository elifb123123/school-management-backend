package com.example.demo.school.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.school.mapper.SchoolMapper;
import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.school.persistence.specification.SchoolSpecification;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.persistence.Student;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.mapper.TeacherMapper;
import com.example.demo.teacher.persistence.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@Transactional
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    // 2. DTO <-> Entity dönüşümleri için
    private final SchoolMapper schoolMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    @Autowired
    public SchoolServiceImpl(SchoolRepository schoolRepository, SchoolMapper schoolMapper, StudentMapper studentMapper, TeacherMapper teacherMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public Page<SchoolResponse> getSchools(String name, Pageable pageable) {
        Specification<School> spec = Specification.where(SchoolSpecification.byName(name));
        Page<School> schoolPage = schoolRepository.findAll(spec, pageable);
        log.info("Retrieved schools page: {}", schoolPage.getNumber());
        return schoolPage.map(schoolMapper::toResponse);
    }

    @Override
    public SchoolResponse getSchoolById(Long schoolId) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new ResourceNotFoundException("School ", "id", schoolId));
        log.info("Retrieved school response: {}", school);
        return schoolMapper.toResponse(school);
    }


    @Override
    public SchoolResponse addNewSchool(SchoolRequest schoolRequest) {
        School saved = schoolRepository.save(schoolMapper.toEntity(schoolRequest));
        log.info("Added new school: {}", schoolRequest.schoolName());
        return schoolMapper.toResponse(saved);
    }

    @Override
    public void deleteSchool(Long schoolId) {

        boolean exists = schoolRepository.existsById(schoolId);

        if (!exists) {
            throw new ResourceNotFoundException("School ", "id", schoolId);
        }
        schoolRepository.deleteById(schoolId);
        log.info("Deleted school: {}", schoolId);
    }

    @Override
    public SchoolResponse updateSchool(Long schoolId, SchoolRequest schoolRequest) {

        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found"));
        schoolMapper.updateSchoolFromDto(schoolRequest, school);
        School updatedSchool = schoolRepository.save(school);
        log.info("Updated school: {}", school);
        return schoolMapper.toResponse(updatedSchool);
    }

    public Page<StudentResponse> getStudentsById(Long schoolId, Pageable pageable) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School ", "id", schoolId);
        }
        Page<Student> students = schoolRepository.findStudentsById(schoolId, pageable);
        log.info("Retrieved students for school  {}", schoolId);
        return students.map(studentMapper::toResponse);
    }

    public Page<TeacherResponse> getTeachersBySchoolId(Long schoolId, Pageable pageable) {

        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School ", "id", schoolId);
        }

        Page<Teacher> teachers = schoolRepository.findTeachersById(schoolId, pageable);
        log.info("Retrieved teachers for school  {}", schoolId);
        return teachers.map(teacherMapper::toResponse);
    }

}
