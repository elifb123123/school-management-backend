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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<StudentResponse> getStudents(String name, String email, LocalDate birthDate, Pageable pageable) {
        Specification<Student> spec = Specification.where(StudentSpecification.byName(name))
                .or(StudentSpecification.byBirthDate(birthDate)).or(StudentSpecification.byEmail(email));
        Page<Student> studentPage = studentRepository.findAll(spec, pageable);
        log.info("Students retrieved successfully");
        return studentPage.map(studentMapper::toResponse);
    }

    public StudentResponse addNewStudent(StudentRequest studentRequest) {
        if (studentRepository.existsByEmail(studentRequest.email())) {
            throw new ResourceAlreadyExistsException("Student", "email", studentRequest.email());
        }

        Student student = studentMapper.toEntity(studentRequest);
        student.setSchool(resolveSchool(studentRequest.schoolName()));

        Student saved = studentRepository.save(student);
        log.info("Saved student successfully");
        return studentMapper.toResponse(saved);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student ", "id", studentId);
        }
        studentRepository.deleteById(studentId);
        log.info("Deleted student successfully");
    }

    @Override
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        student.setName(studentRequest.name());
        student.setDateOfBirth(studentRequest.dateOfBirth());
        student.setSchool(resolveSchool(studentRequest.schoolName()));
        log.info("Updated student successfully");
        // no explicit save() needed — @Transactional + managed entity triggers dirty checking on commit
        return studentMapper.toResponse(student);
    }

    public StudentResponse searchStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student ", "id", studentId));
        log.info("Searched student {} successfully", studentId);
        return studentMapper.toResponse(student);
    }

    private School resolveSchool(String schoolName) {
        return schoolRepository.findSchoolBySchoolName(schoolName)
                .orElseThrow(() -> new ResourceNotFoundException("School ", "school name", schoolName));
    }

    public void addTeacherToStudent(Long studentId, Long teacherId) {
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

    public void deleteTeacherFromStudent(Long studentId, Long teacherId) {
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

    public List<TeacherResponse> getTeachersOfStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        log.info("Teachers of student{} retrieved.", studentId);
        return teacherMapper.toResponseList(studentRepository.findTeachersByStudentId(studentId));
    }

}