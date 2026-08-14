package com.example.demo.school.controller;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.school.service.SchoolService;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.teacher.dto.TeacherResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/school")
public class SchoolController {

    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    //GET /api/schools?page=0&size=10&sort=schoolName,asc
    @GetMapping
    public Page<SchoolResponse> getSchools(Pageable pageable,
                                           @RequestParam(required = false) String name) {

        return schoolService.getSchools(name, pageable);
    }

    @GetMapping(path = "/{schoolId}")
    public SchoolResponse getSchoolById(@PathVariable Long schoolId) {
        return schoolService.getSchoolById(schoolId);
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

    //GET /api/schools/{schoolId}/students?page=0&size=10&sort=schoolName,asc
    @GetMapping("/{schoolId}/students")
    public Page<StudentResponse> getStudentsBySchoolId(@PathVariable Long schoolId, Pageable pageable) {

        return schoolService.getStudentsById(schoolId, pageable);
    }

    //GET /api/schools/{schoolId}/teachers?page=0&size=10&sort=schoolName,asc
    @GetMapping("/{schoolId}/teachers")
    public Page<TeacherResponse> getTeachersBySchoolId(@PathVariable Long schoolId,
                                                       Pageable pageable) {

        return schoolService.getTeachersBySchoolId(schoolId, pageable);
    }
}
