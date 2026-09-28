package com.junior.todomanager.dto;

import com.junior.todomanager.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TaskRequestPostDto {
    @NotBlank
    private String title;
    private String description;
    private LocalDate dueDate;
    @NotNull
    private TaskCategory taskCategory;
}
