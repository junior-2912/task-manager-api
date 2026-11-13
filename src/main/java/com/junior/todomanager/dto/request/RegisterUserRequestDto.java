package com.junior.todomanager.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDto(@NotBlank(message = "Name is required") String name,
                                     @Email @Size(max = 150) @NotBlank(message = "Email is required") String email,
                                     @Size(max = 150) @NotBlank(message = "Password is required") String password) {

}
