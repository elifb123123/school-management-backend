package com.example.demo.teacher.mapper;

import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.teacher.dto.TeacherResponse;
import com.example.demo.teacher.persistence.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "school", ignore = true)
        // resolve School by name in the service (needs a repository lookup)
    Teacher toEntity(TeacherRequest studentRequest);

    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "schoolId", source = "school.id")
    TeacherResponse toResponse(Teacher teacher);

    List<TeacherResponse> toResponseList(List<Teacher> teachers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "school", ignore = true)
    void updateTeacherFromRequest(TeacherRequest teacherRequest, @MappingTarget Teacher teacher);
}
