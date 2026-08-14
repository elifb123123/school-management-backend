package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRequest(

        @NotNull(message = "Name cannot be null")
        String name,
        @Email(message = "Email should be valid")
        String email,
        @NotNull(message = "Password cannot be null")
        String password

) {
}
