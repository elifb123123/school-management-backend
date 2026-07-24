package com.example.demo.school.service;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.school.mapper.SchoolMapper;
import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    // 2. DTO <-> Entity dönüşümleri için
    private final SchoolMapper schoolMapper;

    @Autowired
    public SchoolServiceImpl(SchoolRepository schoolRepository, SchoolMapper schoolMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
    }

    @Override
    public List<SchoolResponse> getSchools() {
        List<School> schoolList = schoolRepository.findAll();
        return schoolMapper.toResponseList(schoolList);
    }

    @Override
    public SchoolResponse getSchoolById(Long schoolId) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new IllegalStateException("School " + schoolId + " does not exist"));
        return schoolMapper.toResponse(school);
    }

    public SchoolResponse getSchoolByName(String schoolName) {
        School school = schoolRepository.findSchoolBySchoolName(schoolName).orElseThrow(() -> new IllegalStateException("School " + schoolName + " does not exist"));
        return schoolMapper.toResponse(school);
    }

    @Override
    public void addNewSchool(SchoolRequest schoolRequest) {
        if (schoolRepository.existsBySchoolName(schoolRequest.schoolName())) {
            throw new IllegalStateException("School name already exists: " + schoolRequest.schoolName());
        }
        schoolRepository.save(schoolMapper.toEntity(schoolRequest));
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
    public SchoolResponse updateSchool(Long schoolId, SchoolRequest schoolRequest) {

        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found"));
        schoolMapper.updateSchoolFromDto(schoolRequest, school);
        School updatedSchool = schoolRepository.save(school);
        return schoolMapper.toResponse(updatedSchool);
    }


}
