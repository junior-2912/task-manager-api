package com.junior.todomanager.dto;

import com.junior.todomanager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusRequestDto {
    private TaskStatus status;
}
