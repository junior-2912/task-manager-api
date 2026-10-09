package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskStatusRequestDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.TaskAlreadyFinishedException;
import com.junior.todomanager.repository.TaskRepository;
import com.junior.todomanager.specification.TaskSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BinaryOperator;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> findAll(Pageable pageable, TaskStatus status, TaskCategory category, String title, Boolean overDue) {
        Specification<Task> specification = Specification.unrestricted();

        if (status != null) specification = specification.and(TaskSpecification.hasStatus(status));

        if (category != null) specification = specification.and(TaskSpecification.hasCategory(category));

        if (title != null && !title.isBlank())
            specification = specification.and(TaskSpecification.titleContains(title));

        if (overDue != null) {
            if (overDue) specification = specification.and(TaskSpecification.overDue());
        }

        return taskRepository.findAll(specification, pageable);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
    }

    @Transactional
    public Task save(TaskRequestPostDto taskRequestPostDto) {
        if (taskRequestPostDto.getDueDate() != null && taskRequestPostDto.getDueDate().isBefore(LocalDateTime.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = new Task(taskRequestPostDto.getTitle(),
                taskRequestPostDto.getDescription(),
                taskRequestPostDto.getDueDate(),
                taskRequestPostDto.getTaskCategory());

        return taskRepository.save(task);
    }

    @Transactional
    public Task update(TaskRequestPutDto taskRequestPutDto, Long id) {
        if (taskRequestPutDto.getDueDate() != null && taskRequestPutDto.getDueDate().isBefore(LocalDateTime.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = findById(id);
        task.setTitle(taskRequestPutDto.getTitle());
        task.setDescription(taskRequestPutDto.getDescription());
        task.setTaskCategory(taskRequestPutDto.getTaskCategory());
        task.setDueDate(taskRequestPutDto.getDueDate());

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteById(Long id) {
        Task task = findById(id);
        if (task.getTaskStatus().equals(TaskStatus.IN_PROGRESS) || task.getTaskStatus().equals(TaskStatus.PENDING)) {
            throw new DeleteTaskException("Actives task cannot be deleted");
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    public void finishTask(Long id) {
        Task task = findById(id);
        if (task.getTaskStatus().equals(TaskStatus.FINISHED) || task.getTaskStatus().equals(TaskStatus.CANCELED)) {
            throw new TaskAlreadyFinishedException("This task has already been finished");
        }
        task.finishTask();
        taskRepository.save(task);
    }

    @Transactional
    public Task changeTaskStatus(Long id, TaskStatusRequestDto taskStatusRequestDto) {
        Task task = findById(id);

        if (task.getTaskStatus().equals(TaskStatus.FINISHED) || task.getTaskStatus().equals(TaskStatus.CANCELED)) {
            throw new TaskAlreadyFinishedException("This task has already been finished");
        }

        if (TaskStatus.FINISHED.equals(taskStatusRequestDto.getStatus())) {
            task.finishTask();
        } else {
            task.setTaskStatus(taskStatusRequestDto.getStatus());
        }

        taskRepository.save(task);

        return task;
    }
}
