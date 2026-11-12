package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequestPutDto(@NotBlank String title, @NotBlank String description, @NotEmpty TaskStatus taskStatus,
                                @NotNull LocalDateTime dueDate , @NotEmpty TaskCategory taskCategory) {
}
