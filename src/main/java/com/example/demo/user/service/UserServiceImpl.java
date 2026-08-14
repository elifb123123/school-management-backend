package com.example.demo.user.service;

import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.school.service.SchoolService;
import com.example.demo.student.service.StudentService;
import com.example.demo.teacher.service.TeacherService;
import com.example.demo.user.dto.*;
import com.example.demo.user.mapper.UserMapper;
import com.example.demo.user.persistence.Role;
import com.example.demo.user.persistence.User;
import com.example.demo.user.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final SchoolService schoolService;

    UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, TeacherService teacherService, StudentService studentService, SchoolService schoolService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.schoolService = schoolService;
    }

    @Override
    @Transactional
    public UserResponse registerPrincipal(PrincipalRegistrationRequest principalRegistrationRequest) {
        UserRequest userRequest = principalRegistrationRequest.userRequest();

        User user = userMapper.toEntity(userRequest);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRole(Role.PRINCIPAL);
        User savedUser = userRepository.save(user);
        schoolService.registerSchool(principalRegistrationRequest.schoolRequest(), savedUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse registerTeacher(TeacherRegistrationRequest teacherRegistrationRequest) {
        UserRequest userRequest = teacherRegistrationRequest.userRequest();
        User user = userMapper.toEntity(userRequest);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRole(Role.TEACHER);
        User savedUser = userRepository.save(user);
        teacherService.registerTeacher(teacherRegistrationRequest.teacherRequest(), savedUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse registerStudent(StudentRegistrationRequest studentRegistrationRequest) {
        User user = userMapper.toEntity(studentRegistrationRequest.userRequest());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(studentRegistrationRequest.userRequest().password()));
        user.setRole(Role.STUDENT);
        User savedUser = userRepository.save(user);
        studentService.registerStudent(studentRegistrationRequest.studentRequest(), savedUser);
        return userMapper.toResponse(savedUser);
    }
}


