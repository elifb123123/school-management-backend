package com.example.demo.school.controller;

import com.example.demo.school.persistence.School;
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
    public List<School> getSchools() {
        return schoolService.getSchools();
    }

    @GetMapping(path = "{schoolId}")
    public School searchSchool(@PathVariable Long schoolId) {
        return schoolService.searchSchool(schoolId);
    }

    @PostMapping
    public ResponseEntity<School> registerNewSchool(@RequestBody School school) {
        schoolService.addNewSchool(school);
        return ResponseEntity.status(201).body(school);
    }

    @DeleteMapping(path = "{schoolId}")
    public void deleteSchool(@PathVariable Long schoolId) {
        schoolService.deleteSchool(schoolId);
    }

    @PutMapping(path = "{schoolId}")
    public ResponseEntity<School> updateSchool(@PathVariable Long schoolId, @RequestParam(required = false) String schoolName) {
        School updated = schoolService.updateSchool(schoolId, schoolName);
        return ResponseEntity.ok().body(updated);
    }
}
