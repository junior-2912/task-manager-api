package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequestPutDto(@NotBlank String title, String description,
                                @NotNull LocalDateTime dueDate , @NotNull TaskCategory taskCategory) {
}
