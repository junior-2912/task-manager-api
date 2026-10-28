package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskResponseDto;
import com.junior.todomanager.dto.TaskStatusRequestDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.TaskAlreadyFinishedException;
import com.junior.todomanager.mapper.TaskMapper;
import com.junior.todomanager.repository.TaskRepository;
import com.junior.todomanager.specification.TaskSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository taskRepository, TaskMapper mapper) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    public Page<TaskResponseDto> findAll(Pageable pageable, TaskStatus status, TaskCategory category, String title, Boolean overdue) {
        Specification<Task> specification = Specification.unrestricted();

        if (status != null) specification = specification.and(TaskSpecification.hasStatus(status));

        if (category != null) specification = specification.and(TaskSpecification.hasCategory(category));

        if (title != null && !title.isBlank())
            specification = specification.and(TaskSpecification.titleContains(title));

        if (Boolean.TRUE.equals(overdue)) specification = specification.and(TaskSpecification.overDue());


        return taskRepository.findAll(specification, pageable).map(mapper::entityToTaskResponse);
    }

    public TaskResponseDto findById(Long id) {
        return mapper.entityToTaskResponse(taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found!")));
    }

    @Transactional
    public TaskResponseDto save(TaskRequestPostDto taskRequestPostDto) {
        Task taskFromMapper = mapper.requestPostToTaskEntity(taskRequestPostDto);
        if (taskFromMapper.getDueDate() != null && taskFromMapper.getDueDate().isBefore(LocalDateTime.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        return mapper.entityToTaskResponse(taskRepository.save(taskFromMapper));
    }

    @Transactional
    public TaskResponseDto update(TaskRequestPutDto taskRequestPutDto, Long id) {
        if (taskRequestPutDto.getDueDate() != null && taskRequestPutDto.getDueDate().isBefore(LocalDateTime.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        mapper.updateTaskFromDto(taskRequestPutDto, task);
        return mapper.entityToTaskResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getTaskStatus().equals(TaskStatus.IN_PROGRESS) || task.getTaskStatus().equals(TaskStatus.PENDING)) {
            throw new DeleteTaskException("Actives task cannot be deleted");
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    public void finishTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getTaskStatus().equals(TaskStatus.FINISHED) || task.getTaskStatus().equals(TaskStatus.CANCELED)) {
            throw new TaskAlreadyFinishedException("This task has already been finished");
        }
        task.finishTask();
        taskRepository.save(task);
    }

    @Transactional
    public TaskResponseDto changeTaskStatus(Long id, TaskStatusRequestDto taskStatusRequestDto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (task.getTaskStatus().equals(TaskStatus.FINISHED) || task.getTaskStatus().equals(TaskStatus.CANCELED)) {
            throw new TaskAlreadyFinishedException("This task has already been finished");
        }

        if (TaskStatus.FINISHED.equals(taskStatusRequestDto.getStatus())) {
            task.finishTask();
        } else {
            task.setTaskStatus(taskStatusRequestDto.getStatus());
        }

        return mapper.entityToTaskResponse(taskRepository.save(task));
    }
}
