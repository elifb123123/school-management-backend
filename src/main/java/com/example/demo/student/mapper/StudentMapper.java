package com.example.demo.student.mapper;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.persistence.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "school", ignore = true)
        // resolve School by name in the service (needs a repository lookup)
    Student toEntity(StudentRequest studentRequest);

    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "schoolName", source = "school.schoolName")
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "school", ignore = true)
        // eşleşmeyi id ye göre yap, id değerinde değişiklik yapma ignore et.
        //school eşleşmesi serviste hallediliyor.
    void updateStudentFromRequest(StudentRequest studentRequest, @MappingTarget Student student);
    //@MappingTarget yeni nesne üretmemesi sadece update etmesi için
}
