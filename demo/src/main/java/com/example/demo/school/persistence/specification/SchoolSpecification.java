package com.example.demo.school.persistence.specification;

import com.example.demo.school.persistence.School;
import org.springframework.data.jpa.domain.Specification;

public class SchoolSpecification {
    public static Specification<School> byName(String name) {
        return (root, query, criteriaBuilder) -> {
            // 1. Parametre null veya boşsa filtreyi yok say
            if (name == null || name.trim().isEmpty()) {
                return null;
            }

            // 2. Büyük/küçük harf duyarsız ve "%" ile içeren (contains) arama
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("schoolName")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }
}
