package com.example.demo.teacher.persistence;

import com.example.demo.school.persistence.School;
import com.example.demo.student.persistence.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor

public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "schoolId")
    private School school;

    @ManyToMany
    @JoinTable(
            name = "course",
            joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id")
    )
    private Set<Student> students = new HashSet<>();


    public Teacher(String name) {
        this.name = name;
    }

}
