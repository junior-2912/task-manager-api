package com.junior.todomanager.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDto(@NotEmpty(message = "Email is required") String email,
                              @NotEmpty(message = "Password is required") String password) {
}
