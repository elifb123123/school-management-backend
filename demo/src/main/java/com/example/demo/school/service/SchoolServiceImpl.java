package com.example.demo.school.service;

import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;

    @Autowired
    public SchoolServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public List<School> getSchools() {
        return schoolRepository.findAll();
    }

    @Override
    public void addNewSchool(School school) {
        Optional<School> schoolOptional = schoolRepository.findSchoolBySchoolName(school.getSchoolName());
        if (schoolOptional.isPresent()) {
            throw new IllegalStateException("School name already exists.");
        }
        schoolRepository.save(school);
        System.out.println(school);
    }

    @Override
    public void deleteSchool(Long schoolId) {
        System.out.println("Service received id: " + schoolId);

        boolean exists = schoolRepository.existsById(schoolId);

        if (!exists) {
            throw new IllegalStateException("School " + schoolId + " does not exist");
        }
        schoolRepository.deleteById(schoolId);
    }

    @Override
    public School updateSchool(Long schoolId, String schoolName) {

        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new IllegalStateException("School " + schoolId + " does not exist"));

        if (schoolName != null && schoolName.length() > 0 &&
                !Objects.equals(school.getSchoolName(), schoolName)) {
            school.setSchoolName(schoolName);
        }
        return school;
    }

    @Override
    public School searchSchool(Long schoolId) {
        System.out.println(schoolRepository.findById(schoolId));
        return schoolRepository.findById(schoolId).orElseThrow(() -> new IllegalStateException("School " + schoolId + " does not exist"));
    }
}
