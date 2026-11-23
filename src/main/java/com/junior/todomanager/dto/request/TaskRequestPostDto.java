package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestPostDto {
    @NotBlank
    @Size(max = 150)
    private String title;
    @Size(max = 300)
    private String description;
    @NotNull
    private LocalDateTime dueDate;
    @NotNull
    private TaskCategory taskCategory;
}
