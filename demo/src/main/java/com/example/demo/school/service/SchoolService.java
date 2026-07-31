package com.example.demo.school.service;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SchoolService {

    List<SchoolResponse> getSchools(String name, Pageable pageable);

    SchoolResponse getSchoolById(Long schoolId);

    SchoolResponse getSchoolByName(String name);

    void addNewSchool(SchoolRequest schoolRequest);

    void deleteSchool(Long schoolId);

    SchoolResponse updateSchool(Long schoolId, SchoolRequest schoolRequest);

    List<StudentResponse> getStudentsById(Long schoolId, Pageable pageable);

    List<TeacherResponse> getTeachersBySchoolId(Long schoolId, Pageable pageable);

}
