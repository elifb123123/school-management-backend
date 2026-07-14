package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.LocalDate;
import java.time.Month;
import java.util.List;


@Configuration

public class StudentConfig {
    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository){
        return args ->{
            Student ayse = new Student(
                    "ayse",
                    LocalDate.of(2003, Month.MARCH,5)
            );

            Student alex = new Student(
                    "alex",
                    LocalDate.of(2003, Month.MARCH,5)
            );


            repository.saveAll(
                    List.of(ayse, alex)
            );
        };
    }
}
