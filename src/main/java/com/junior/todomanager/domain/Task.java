package com.junior.todomanager.domain;


import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@EqualsAndHashCode(of = "id")
public class Task implements Serializable {
    private static final Long serialLongID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private TaskStatus taskStatus;
    private LocalDate dueDate;
    // Enum was the choice because a class category is not necessary for a personal project.
    @Column(name = "category")
    @Enumerated(value = EnumType.STRING)
    private TaskCategory taskCategory;


    //TODO implements this method
    public void finishTask() {
        setTaskStatus(TaskStatus.FINISHED);
    }
}
