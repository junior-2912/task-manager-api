package com.junior.todomanager.dto.request;


import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(@NotBlank(message = "Name is required") String name,
                                     @NotBlank(message = "Email is required") String email,
                                     @NotBlank(message = "Password is required") String password) {

}
