package com.junior.todomanager.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequestDto(@NotEmpty(message = "Name is required") String name,
                                     @NotEmpty(message = "Email is required") String email,
                                     @NotEmpty(message = "Password is required") String password) {

}
