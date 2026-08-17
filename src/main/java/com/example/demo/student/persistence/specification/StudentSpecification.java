package com.example.demo.student.persistence.specification;

import com.example.demo.student.persistence.Student;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class StudentSpecification {

    public static Specification<Student> byName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            } else {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("name")),
                        "%" + name.toLowerCase() + "%");
            }

        };

    }

    public static Specification<Student> byEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return null;
            } else {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("email")), "%" + email.toLowerCase() + "%");
            }
        };

    }


    public static Specification<Student> byBirthDate(LocalDate dateOfBirth) {
        return (root, query, criteriaBuilder) -> {
            if (dateOfBirth == null) {
                return null;
            } else {
                return criteriaBuilder.equal(root.get("dateOfBirth"), dateOfBirth);
            }
        };
    }

}

