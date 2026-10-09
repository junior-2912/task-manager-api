package com.junior.todomanager.domain;


import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
public class Task implements Serializable {
    private static final Long serialLongID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private TaskStatus taskStatus;

    @Column(nullable = false)
    private LocalDateTime dueDate;
    // Enum was the choice because a class category is not necessary for a personal project.
    @Column(name = "category")
    @Enumerated(value = EnumType.STRING)
    private TaskCategory taskCategory;

    public Task(String title, String description, LocalDateTime dueDate, TaskCategory taskCategory) {
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.PENDING;
        this.dueDate = dueDate;
        this.taskCategory = taskCategory;
    }

    public void finishTask() {
        setTaskStatus(TaskStatus.FINISHED);
    }
}
