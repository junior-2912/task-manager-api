package com.junior.todomanager.dto;

import com.junior.todomanager.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestPostDto {
    @NotBlank
    private String title;
    private String description;
    private LocalDateTime dueDate;
    @NotNull
    private TaskCategory taskCategory;
}
