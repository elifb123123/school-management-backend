package com.example.demo.config;

import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.persistence.Student;
import com.example.demo.student.persistence.StudentRepository;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.persistence.Branch;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import com.example.demo.teacher.service.TeacherService;
import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;


@Configuration
public class DataConfig {


    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository, SchoolRepository schoolRepository,
                                         TeacherRepository teacherRepository, UserService userService,
                                         TeacherService teacherService) {
        return args -> {
            if (schoolRepository.count() == 0) {
                School school1 = new School("School 1");
                school1.setAddress("123 Main St, Springfield");
                School school2 = new School("School 2");
                school2.setAddress("456 Oak Ave, Shelbyville");
                schoolRepository.saveAll(List.of(school1, school2));
            }

            if (studentRepository.findAll().isEmpty()) {
                Student ayse = new Student("ayse", "ayse@gmail.com", LocalDate.of(2003, Month.MARCH, 5));
                Student alex = new Student("alex", "alex@gmail.com", LocalDate.of(2003, Month.MARCH, 5));
                ayse.setSchool(schoolRepository.findSchoolBySchoolName("School 1").orElse(null));
                alex.setSchool(schoolRepository.findSchoolBySchoolName("School 1").orElse(null));
                studentRepository.saveAll(List.of(ayse, alex));
            }

            if (teacherRepository.findAll().isEmpty()) {
                School school1 = schoolRepository.findSchoolBySchoolName("School 1")
                        .orElseThrow(() -> new RuntimeException("School 1 bulunamadı!"));
                School school2 = schoolRepository.findSchoolBySchoolName("School 2")
                        .orElseThrow(() -> new RuntimeException("School 2 bulunamadı!"));

                userService.registerTeacher(new TeacherRegistrationRequest(
                        new UserRequest("john.smith", "jhon@gmail.com", "password"),
                        new TeacherRequest(Branch.CHEMISTRY, school1.getId())
                ));

                userService.registerTeacher(new TeacherRegistrationRequest(
                        new UserRequest("maria.garcia", "maria@gmail.com", "password"),
                        new TeacherRequest(Branch.MATHEMATICS, school2.getId())
                ));

                Teacher john = teacherRepository.findAll().stream()
                        .filter(t -> t.getUser().getUsername().equals("john.smith"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("John Smith bulunamadı!"));

                Student ayse = studentRepository.findStudentByName("ayse")
                        .orElseThrow(() -> new RuntimeException("Ayşe bulunamadı!"));
                Student alex = studentRepository.findStudentByName("alex")
                        .orElseThrow(() -> new RuntimeException("Alex bulunamadı!"));

                teacherService.linkStudent(john.getId(), ayse.getId());
                teacherService.linkStudent(john.getId(), alex.getId());
            }
        };
    }
}
