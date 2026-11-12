package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestPatchDto {
    private String title;
    private String description;
    private LocalDateTime dueDate;

    private TaskCategory taskCategory;
}
