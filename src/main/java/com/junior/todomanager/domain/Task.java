package com.junior.todomanager.domain;


import com.junior.todomanager.enums.TaskStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Task implements Serializable {
    private Long id;
    private String description;
    private TaskStatus taskStatus;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    @ManyToOne
    private Category category;

    public void finishTask() {
        setTaskStatus(TaskStatus.FINISHED);
    }
}
