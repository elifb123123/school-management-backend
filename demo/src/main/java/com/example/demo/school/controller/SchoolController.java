package com.example.demo.school.controller;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.school.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<SchoolResponse> getSchools() {
        return schoolService.getSchools();
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
    public ResponseEntity<Void> registerNewSchool(@RequestBody SchoolRequest schoolRequest) {
        schoolService.addNewSchool(schoolRequest);
        return ResponseEntity.status(201).build();//201 for created
    }

    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long schoolId) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{schoolId}")
    public ResponseEntity<SchoolResponse> updateSchool(@PathVariable Long schoolId, @RequestBody SchoolRequest schoolRequest) {
        SchoolResponse updated = schoolService.updateSchool(schoolId, schoolRequest);
        return ResponseEntity.ok().body(updated);
    }
}
