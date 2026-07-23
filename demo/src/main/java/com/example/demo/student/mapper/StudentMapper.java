package com.example.demo.student.mapper;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.persistence.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "school", ignore = true)
        // resolve School by name in the service (needs a repository lookup)
    Student toEntity(StudentRequest studentRequest);

    @Mapping(target = "schoolName", source = "school.schoolName")
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);
}
