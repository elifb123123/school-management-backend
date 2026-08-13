package com.example.demo.user.service;

import com.example.demo.teacher.service.TeacherService;
import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;
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

    UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, TeacherService teacherService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.teacherService = teacherService;
    }

    @Override
    public UserResponse registerPrincipal(UserRequest userRequest) {

        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRole(Role.PRINCIPAL);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse registerTeacher(TeacherRegistrationRequest teacherRegistrationRequest) {
        UserRequest userRequest = teacherRegistrationRequest.userRequest();
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRole(Role.TEACHER);
        User savedUser = userRepository.save(user);
        teacherService.registerTeacher(teacherRegistrationRequest.teacherRequest(), savedUser);
        return userMapper.toResponse(savedUser);
    }

//    @Override
//    public UserResponse registerStudent(UserRequest userRequest) {
//
//        User user = userMapper.toEntity(userRequest);
//        user.setPassword(passwordEncoder.encode(userRequest.password()));
//        User savedUser = userRepository.save(user);
//        return userMapper.toResponse(savedUser);
//    }
}


