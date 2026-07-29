package com.example.demo.school.persistence;

import com.example.demo.student.persistence.Student;
import com.example.demo.teacher.persistence.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schoolId;

    @Column(nullable = false, unique = true)
    private String schoolName;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"school", "teachers"})
    private List<Student> students;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"school", "students"})
    private List<Teacher> teachers;

    public School(String schoolName) {
        this.schoolName = schoolName;
    }


}
