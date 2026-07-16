package com.example.demo.student;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;


@Entity
@Table
@Setter
@Getter
@ToString
public class Student {
    @Id
    @SequenceGenerator(
            name = "student_seq",
            sequenceName = "student_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_seq"
    )
    private Long id;

    private String name;
    private LocalDate DateOfBirth;
    @Transient
    private Integer age;


    public Student() {
    }

    public Student(Long id, String name, LocalDate DateOfBirth) {
        this.id = id;
        this.name = name;
        this.DateOfBirth = DateOfBirth;
    }

    public Student(String name, LocalDate DateOfBirth) {
        this.name = name;
        this.DateOfBirth = DateOfBirth;
    }


    public Integer getAge() {

        return Period.between(DateOfBirth, LocalDate.now()).getYears();
    }

}
