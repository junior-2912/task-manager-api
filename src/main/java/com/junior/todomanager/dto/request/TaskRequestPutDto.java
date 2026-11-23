package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequestPutDto(@NotBlank @Size(max = 150) String title, @Size(max = 300) String description,
                                @NotNull LocalDateTime dueDate , @NotNull TaskCategory taskCategory) {
}
