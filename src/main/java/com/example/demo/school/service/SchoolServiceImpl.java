package com.example.demo.school.service;

import com.example.demo.exception.ResourceAlreadyExistsException;
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
import com.example.demo.user.persistence.Role;
import com.example.demo.user.persistence.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public Page<SchoolResponse> getSchools(String name, Pageable pageable) {
        Specification<School> spec = Specification.where(SchoolSpecification.byName(name));
        Page<School> schoolPage = schoolRepository.findAll(spec, pageable);
        log.info("Retrieved schools page: {}", schoolPage.getNumber());
        return schoolPage.map(schoolMapper::toResponse);
    }


    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #schoolId)")
    public SchoolResponse getSchoolById(Long schoolId) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new ResourceNotFoundException("School ", "id", schoolId));
        log.info("Retrieved school response: {}", school);
        return schoolMapper.toResponse(school);
    }


    public SchoolResponse registerSchool(SchoolRequest schoolRequest, User user) {
        // Bu fonksiyon RegisterPrincipal tarafından kullanılıyor. RegisterPrincipal için herhangi bir güvenlik kontrolü yok çünkü herkes kayıt olabilir.
        // bu fonksiyonu doğrudan çağıran bir requestde yok bu yüzden authentication nesnesi üzerinden kontrol yapamıyoruz.
        // güvenlik kontrolünü preAuthorize ile yapamıyoruz. Dolayısıyla güvenlik kontrolünü manuel yazdım.
        if (user.getRole() != Role.PRINCIPAL && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Sadece principal veya admin rolündeki kullanıcılar okul oluşturabilir.");
        }
        if (schoolRepository.findByUser(user).isPresent()) {
            throw new ResourceAlreadyExistsException("School", "user", user.getId());
        }
        School school = schoolMapper.toEntity(schoolRequest);
        school.setUser(user);
        school = schoolRepository.save(school);
        log.info("Added new school: {}", schoolRequest.schoolName());
        return schoolMapper.toResponse(school);
    }


    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #schoolId)")
    public void deleteSchool(Long schoolId) {

        boolean exists = schoolRepository.existsById(schoolId);

        if (!exists) {
            throw new ResourceNotFoundException("School ", "id", schoolId);
        }
        schoolRepository.deleteById(schoolId);
        log.info("Deleted school: {}", schoolId);
    }


    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #schoolId)")
    public SchoolResponse updateSchool(Long schoolId, SchoolRequest schoolRequest) {

        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new ResourceNotFoundException("School ", "id", schoolId));
        schoolMapper.updateSchoolFromDto(schoolRequest, school);
        School updatedSchool = schoolRepository.save(school);
        log.info("Updated school: {}", school);
        return schoolMapper.toResponse(updatedSchool);
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #schoolId)")
    public Page<StudentResponse> getStudentsById(Long schoolId, Pageable pageable) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School ", "id", schoolId);
        }
        Page<Student> students = schoolRepository.findStudentsById(schoolId, pageable);
        log.info("Retrieved students for school  {}", schoolId);
        return students.map(studentMapper::toResponse);
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #schoolId)")
    public Page<TeacherResponse> getTeachersBySchoolId(Long schoolId, Pageable pageable) {

        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School ", "id", schoolId);
        }

        Page<Teacher> teachers = schoolRepository.findTeachersById(schoolId, pageable);
        log.info("Retrieved teachers for school  {}", schoolId);
        return teachers.map(teacherMapper::toResponse);
    }


}
