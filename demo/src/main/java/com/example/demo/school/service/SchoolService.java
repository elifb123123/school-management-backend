package com.example.demo.school.service;

import com.example.demo.school.persistence.School;

import java.util.List;

public interface SchoolService {

    List<School> getSchools();

    void addNewSchool(School school);

    void deleteSchool(Long schoolId);

    School updateSchool(Long schoolId, String schoolName);

    School searchSchool(Long schoolId);
}
