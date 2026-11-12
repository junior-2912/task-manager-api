package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequestPutDto(@NotBlank String title, @NotBlank String description,
                                @NotNull LocalDateTime dueDate , @NotNull TaskCategory taskCategory) {
}
