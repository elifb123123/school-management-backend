package com.example.demo.school.service;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.student.dto.StudentResponse;

import java.util.List;

public interface SchoolService {

    List<SchoolResponse> getSchools();

    SchoolResponse getSchoolById(Long schoolId);

    SchoolResponse getSchoolByName(String name);

    void addNewSchool(SchoolRequest schoolRequest);

    void deleteSchool(Long schoolId);

    SchoolResponse updateSchool(Long schoolId, SchoolRequest schoolRequest);

    List<StudentResponse> getStudentsById(Long studentId);

}
