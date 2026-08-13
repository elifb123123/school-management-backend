package com.example.demo.teacher.persistence;

import com.example.demo.school.persistence.School;
import com.example.demo.student.persistence.Student;
import com.example.demo.user.persistence.User;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id", nullable = false)
    private School school;

    @ManyToMany
    @JoinTable(
            name = "student_teacher",
            joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id")
    )
    private Set<Student> students = new HashSet<>();


}
