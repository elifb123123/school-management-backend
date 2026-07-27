package com.example.demo.student.persistence;


import com.example.demo.school.persistence.School;
import com.example.demo.teacher.persistence.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;


@Entity
@Table
@Setter
@Getter
@ToString
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private LocalDate dateOfBirth;
    @Transient
    private Integer age;


    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"students", "teachers"})
    @ToString.Exclude
    private School school;

    @JsonIgnoreProperties({"students", "school"})
    @ManyToMany
    @ToString.Exclude
    private Set<Teacher> teachers;


    public Student() {
    }

    public Student(Long id, String name, String email, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public Student(String name, String email, LocalDate dateOfBirth) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }


    public Integer getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }


}
