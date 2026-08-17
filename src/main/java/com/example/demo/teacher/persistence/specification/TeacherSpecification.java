package com.example.demo.teacher.persistence.specification;

import com.example.demo.teacher.persistence.Teacher;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {

    public static Specification<Teacher> byName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            } else {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("name")), "%" + name.toLowerCase() + "%");
            }
        };
    }

}
