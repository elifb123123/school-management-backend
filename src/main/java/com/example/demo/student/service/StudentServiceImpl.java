package com.example.demo.student.service;

import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.persistence.Student;
import com.example.demo.student.persistence.StudentRepository;
import com.example.demo.student.persistence.specification.StudentSpecification;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.mapper.TeacherMapper;
import com.example.demo.teacher.service.TeacherService;
import com.example.demo.user.persistence.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final StudentMapper studentMapper;
    private final TeacherService teacherService;//owning side teacher bu yuzden student_teacher ilişkilerina ait fonksiyonlar oradan geliyor.
    private final TeacherMapper teacherMapper;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              SchoolRepository schoolRepository,
                              StudentMapper studentMapper,
                              TeacherService teacherService,
                              TeacherMapper teacherMapper) {
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.studentMapper = studentMapper;
        this.teacherService = teacherService;
        this.teacherMapper = teacherMapper;
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudents(String name, String email, LocalDate birthDate, Pageable pageable) {
        Specification<Student> spec = Specification.where(StudentSpecification.byName(name))
                .and(StudentSpecification.byBirthDate(birthDate)).and(StudentSpecification.byEmail(email));
        Page<Student> studentPage = studentRepository.findAll(spec, pageable);
        log.info("Students retrieved successfully");
        return studentPage.map(studentMapper::toResponse);
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, #studentRequest.schoolId())")
    public StudentResponse registerStudent(StudentRequest studentRequest, User user) {

        Student student = studentMapper.toEntity(studentRequest);
        Long schoolId = studentRequest.schoolId();
        student.setSchool(resolveSchool(schoolId));
        student.setUser(user);
        Student saved = studentRepository.save(student);
        log.info("Saved student successfully");
        return studentMapper.toResponse(saved);
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId))")
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student ", "id", studentId);
        }
        studentRepository.deleteById(studentId);
        log.info("Deleted student successfully");
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId))")
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        studentMapper.updateStudentFromRequest(studentRequest, student);
        // school kasıtlı olarak dokunulmuyor
        log.info("Updated student successfully");
        // student, findById ile çekildiği için hâlâ Hibernate'in izlediği (managed) bir nesne;
        // @Transactional commit olurken değişiklik fark edilip otomatik UPDATE atılır, save() gerekmez.
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId)) " +
            "|| @studentSecurity.isSelf(authentication.name, #studentId)")
    public StudentResponse searchStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student ", "id", studentId));
        log.info("Searched student {} successfully", studentId);
        return studentMapper.toResponse(student);
    }

    private School resolveSchool(Long schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School ", "school id", schoolId));
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId))" +
            " && @schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))")
    public void linkTeacherToStudent(Long studentId, Long teacherId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        if (!teacherService.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }
        if (teacherService.existsRelation(teacherId, studentId)) {          // studentRepository değil, teacherService
            throw new ResourceAlreadyExistsException("Student-Teacher relation", "studentId-teacherId", studentId + "-" + teacherId);
        }
        teacherService.linkStudent(teacherId, studentId);//  studentRepository değil, teacherService

        log.info("Teacher {} and student{} linked by student ", teacherId, studentId);
    }

    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId))" +
            " && @schoolSecurity.isPrincipalOf(authentication.name, @teacherSecurity.findSchoolId(#teacherId))")
    public void unlinkTeacherFromStudent(Long studentId, Long teacherId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        if (!teacherService.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", "id", teacherId);
        }
        if (!teacherService.existsRelation(teacherId, studentId)) {
            throw new ResourceNotFoundException("Student-Teacher relation", "studentId-teacherId", studentId + "-" + teacherId);
        }
        teacherService.unlinkStudent(teacherId, studentId);
        log.info("Teacher {} and student{} unlinked by student ", teacherId, studentId);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@schoolSecurity.isPrincipalOf(authentication.name, @studentSecurity.findSchoolId(#studentId)) " +
            "|| @studentSecurity.isSelf(authentication.name, #studentId)")
    public List<TeacherResponse> getTeachersOfStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        log.info("Teachers of student{} retrieved.", studentId);
        return teacherMapper.toResponseList(studentRepository.findTeachersByStudentId(studentId));
    }

}