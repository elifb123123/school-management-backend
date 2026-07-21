package com.example.demo.config;

import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.persistence.Student;
import com.example.demo.student.persistence.StudentRepository;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Configuration
public class DataConfig {


    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository, TeacherRepository teacherRepository, SchoolRepository schoolRepository) {
        return args -> {
            if (schoolRepository.count() == 0) {
                School school1 = new School("School 1");
                School school2 = new School("School 2");
                schoolRepository.saveAll(List.of(school1, school2));
            }

            if (studentRepository.findAll().isEmpty()) {
                Student ayse = new Student("ayse", LocalDate.of(2003, Month.MARCH, 5));
                Student alex = new Student("alex", LocalDate.of(2003, Month.MARCH, 5));
                ayse.setSchool(schoolRepository.findSchoolBySchoolName("School 1").orElse(null));
                alex.setSchool(schoolRepository.findSchoolBySchoolName("School 2").orElse(null));
                studentRepository.saveAll(List.of(ayse, alex));
            }

            if (teacherRepository.findAll().isEmpty()) {
                Teacher john = new Teacher();
                john.setName("John Smith");
                john.setSchool(schoolRepository.findSchoolBySchoolName("School 1").orElse(null));
                Student ayse = studentRepository.findStudentByName("ayse")
                        .orElseThrow(() -> new RuntimeException("Ayşe bulunamadı!"));

                Student alex = studentRepository.findStudentByName("alex")
                        .orElseThrow(() -> new RuntimeException("Alex bulunamadı!"));
                // Null riski kalmadığı için List.of güvenle kullanılabilir
                john.setStudents(Set.of(ayse, alex));
                Teacher maria = new Teacher();
                maria.setName("Maria Garcia");
                maria.setSchool(schoolRepository.findSchoolBySchoolName("School 2").orElse(null));

                teacherRepository.saveAll(
                        List.of(john, maria)
                );
            }
        };
    }
}
