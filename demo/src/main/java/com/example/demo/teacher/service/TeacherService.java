package com.example.demo.teacher.service;

import com.example.demo.teacher.persistence.Teacher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TeacherService {
    List<Teacher> getTeachers();
}
