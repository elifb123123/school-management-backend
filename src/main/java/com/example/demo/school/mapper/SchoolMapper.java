package com.example.demo.school.mapper;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.school.dto.SchoolResponse;
import com.example.demo.school.persistence.School;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    SchoolResponse toResponse(School school);

    School toEntity(SchoolRequest request);

    List<SchoolResponse> toResponseList(List<School> schools);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSchoolFromDto(SchoolRequest dto, @MappingTarget School entity);
    //@MappingTarget diyerek MapStruct'a "Yeni bir School oluşturma, sana parametre olarak verdiğim entity'nin İÇİNİ GÜNCELLE" demiş oluyorsun.

    /* nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
     * Bu ayar olmazsa MapStruct veritabanından gelen adresi silip yerine null yazar*/

}
