package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusRequestDto {
    @NotNull
    private TaskStatus status;
}
