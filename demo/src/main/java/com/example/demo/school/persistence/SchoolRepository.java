package com.example.demo.school.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findSchoolBySchoolName(String schoolName);
}
