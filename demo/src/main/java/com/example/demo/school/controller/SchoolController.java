package com.example.demo.school.controller;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.school.service.SchoolService;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/school")
public class SchoolController {

    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping
    public List<SchoolResponse> getSchools(@RequestParam(required = false, defaultValue = "10") int pageSize,
                                           @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                           @RequestParam(required = false, defaultValue = "id") String sortBy,
                                           @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                           @RequestParam(required = false) String name) {
        Sort sort = Sort.by(sortBy);
        if ("DESC".equalsIgnoreCase(sortDir)) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return schoolService.getSchools(name, pageable);
    }

    @GetMapping(path = "/{schoolId}")
    public SchoolResponse getSchoolById(@PathVariable Long schoolId) {
        return schoolService.getSchoolById(schoolId);
    }

    @GetMapping("/search")
    public SchoolResponse getSchoolByName(@RequestParam String name) {
        return schoolService.getSchoolByName(name);
    }

    @PostMapping
    public ResponseEntity<Void> registerNewSchool(@RequestBody @Valid SchoolRequest schoolRequest) {
        schoolService.addNewSchool(schoolRequest);
        return ResponseEntity.status(201).build();//201 for created
    }

    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long schoolId) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{schoolId}")
    public ResponseEntity<SchoolResponse> updateSchool(@PathVariable Long schoolId, @RequestBody @Valid SchoolRequest schoolRequest) {
        SchoolResponse updated = schoolService.updateSchool(schoolId, schoolRequest);
        return ResponseEntity.ok().body(updated);
    }

    @GetMapping("/{schoolId}/students")
    public List<StudentResponse> getStudentsBySchoolId(@PathVariable Long schoolId,
                                                       @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                                       @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "ASC") String sortDir) {
        Sort sort = Sort.by(sortBy);
        if ("DESC".equalsIgnoreCase(sortDir)) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return schoolService.getStudentsById(schoolId, pageable);
    }

    @GetMapping("/{schoolId}/teachers")
    public List<TeacherResponse> getTeachersBySchoolId(@PathVariable Long schoolId,
                                                       @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                                       @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "ASC") String sortDir) {
        Sort sort = Sort.by(sortBy);
        if ("DESC".equalsIgnoreCase(sortDir)) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return schoolService.getTeachersBySchoolId(schoolId, pageable);
    }
}
