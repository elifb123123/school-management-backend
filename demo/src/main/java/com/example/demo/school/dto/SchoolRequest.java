package com.example.demo.school.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolRequest {
    private String schoolName;

    public SchoolRequest() {
    }

    public SchoolRequest(String schoolName) {
        this.schoolName = schoolName;
    }

}
