package com.example.demo.school.service;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.user.persistence.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchoolService {

    Page<SchoolResponse> getSchools(String name, Pageable pageable);

    SchoolResponse getSchoolById(Long schoolId);

    SchoolResponse registerSchool(SchoolRequest schoolRequest, User user);

    void deleteSchool(Long schoolId);

    SchoolResponse updateSchool(Long schoolId, SchoolRequest schoolRequest);

    Page<StudentResponse> getStudentsById(Long schoolId, Pageable pageable);

    Page<TeacherResponse> getTeachersBySchoolId(Long schoolId, Pageable pageable);

}
