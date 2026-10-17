package com.junior.todomanager.domain;


import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
