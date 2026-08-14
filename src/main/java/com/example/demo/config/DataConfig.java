package com.example.demo.config;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.persistence.Student;
import com.example.demo.student.persistence.StudentRepository;
import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.persistence.Branch;
import com.example.demo.teacher.persistence.Teacher;
import com.example.demo.teacher.persistence.TeacherRepository;
import com.example.demo.teacher.service.TeacherService;
import com.example.demo.user.dto.PrincipalRegistrationRequest;
import com.example.demo.user.dto.StudentRegistrationRequest;
import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;


@Configuration
public class DataConfig {


    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository, SchoolRepository schoolRepository,
                                         TeacherRepository teacherRepository, UserService userService,
                                         TeacherService teacherService) {
        return args -> {
            if (schoolRepository.count() == 0) {
                userService.registerPrincipal(new PrincipalRegistrationRequest(
                        new UserRequest("principal1", "principal1@gmail.com", "password"),
                        new SchoolRequest("School 1", "123 Main St, Springfield")
                ));

                userService.registerPrincipal(new PrincipalRegistrationRequest(
                        new UserRequest("principal2", "principal2@gmail.com", "password"),
                        new SchoolRequest("School 2", "456 Oak Ave, Shelbyville")
                ));
            }

            if (studentRepository.findAll().isEmpty()) {
                School school1 = schoolRepository.findSchoolBySchoolName("School 1")
                        .orElseThrow(() -> new RuntimeException("School 1 bulunamadı!"));

                userService.registerStudent(new StudentRegistrationRequest(
                        new UserRequest("ayse", "ayse@gmail.com", "password"),
                        new StudentRequest(LocalDate.of(2003, Month.MARCH, 5), school1.getId())
                ), "principal1@gmail.com");

                userService.registerStudent(new StudentRegistrationRequest(
                        new UserRequest("alex", "alex@gmail.com", "password"),
                        new StudentRequest(LocalDate.of(2003, Month.MARCH, 5), school1.getId())
                ), "principal1@gmail.com");
            }

            if (teacherRepository.findAll().isEmpty()) {
                School school1 = schoolRepository.findSchoolBySchoolName("School 1")
                        .orElseThrow(() -> new RuntimeException("School 1 bulunamadı!"));
                School school2 = schoolRepository.findSchoolBySchoolName("School 2")
                        .orElseThrow(() -> new RuntimeException("School 2 bulunamadı!"));

                userService.registerTeacher(new TeacherRegistrationRequest(
                        new UserRequest("john.smith", "jhon@gmail.com", "password"),
                        new TeacherRequest(Branch.CHEMISTRY, school1.getId())
                ), "principal1@gmail.com");

                userService.registerTeacher(new TeacherRegistrationRequest(
                        new UserRequest("maria.garcia", "maria@gmail.com", "password"),
                        new TeacherRequest(Branch.MATHEMATICS, school2.getId())
                ), "principal2@gmail.com");

                Teacher john = teacherRepository.findAll().stream()
                        .filter(t -> t.getUser().getName().equals("john.smith"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("John Smith bulunamadı!"));

                Student ayse = studentRepository.findAll().stream()
                        .filter(s -> s.getUser().getName().equals("ayse"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Ayşe bulunamadı!"));
                Student alex = studentRepository.findAll().stream()
                        .filter(s -> s.getUser().getName().equals("alex"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Alex bulunamadı!"));

                teacherService.linkStudent(john.getId(), ayse.getId());
                teacherService.linkStudent(john.getId(), alex.getId());
            }
        };
    }
}
