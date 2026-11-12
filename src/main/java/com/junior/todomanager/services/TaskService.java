package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.request.TaskRequestPostDto;
import com.junior.todomanager.dto.request.TaskRequestPatchDto;
import com.junior.todomanager.dto.request.TaskRequestPutDto;
import com.junior.todomanager.dto.response.TaskResponseDto;
import com.junior.todomanager.dto.request.TaskStatusRequestDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.TaskAlreadyFinishedException;
import com.junior.todomanager.mapper.TaskMapper;
import com.junior.todomanager.repository.TaskRepository;
import com.junior.todomanager.specification.TaskSpecification;
import com.junior.todomanager.util.AuthenticatedUserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;

    public TaskService(TaskRepository taskRepository, TaskMapper mapper, AuthenticatedUserProvider authenticatedUserProvider, RepositoryMethodInvocationListener repositoryMethodInvocationListener) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.repositoryMethodInvocationListener = repositoryMethodInvocationListener;
    }

    public Page<TaskResponseDto> findAll(Pageable pageable, TaskStatus status, TaskCategory category, String title, Boolean overdue) {
        Specification<Task> specification = Specification.unrestricted();

        if (status != null) specification = specification.and(TaskSpecification.hasStatus(status));

        if (category != null) specification = specification.and(TaskSpecification.hasCategory(category));

        if (title != null && !title.isBlank())
            specification = specification.and(TaskSpecification.titleContains(title));

        if (Boolean.TRUE.equals(overdue)) specification = specification.and(TaskSpecification.overDue());

        specification = specification.and(TaskSpecification.withUserId(authenticatedUserProvider.getAuthenticatedUser().getId()));

        return taskRepository.findAll(specification, pageable).map(mapper::entityToTaskResponse);
    }

    public TaskResponseDto findById(Long id) {
        return mapper.entityToTaskResponse(taskRepository.findByIdAndUserId(id, authenticatedUserProvider.getAuthenticatedUser().getId()).orElseThrow(() -> new ResourceNotFoundException("Task not found")));
    }

    @Transactional
    public TaskResponseDto save(TaskRequestPostDto taskRequestPostDto) {
        Task taskFromMapper = mapper.requestPostToTaskEntity(taskRequestPostDto, authenticatedUserProvider.getAuthenticatedUser());
        if (taskFromMapper.getDueDate() != null && taskFromMapper.getDueDate().isBefore(LocalDateTime.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        return mapper.entityToTaskResponse(taskRepository.save(taskFromMapper));
    }

    @Transactional
    public TaskResponseDto patch(TaskRequestPatchDto taskRequestPatchDto, Long id) {
        if (taskRequestPatchDto.getDueDate() != null && taskRequestPatchDto.getDueDate().isBefore(LocalDateTime.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = taskRepository.findByIdAndUserId(id, authenticatedUserProvider.getAuthenticatedUser().getId()).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        mapper.updateTaskFromDto(taskRequestPatchDto, task);
        return mapper.entityToTaskResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponseDto replace(TaskRequestPutDto taskRequestPutDto, Long id) {
        Task taskPersisted = taskRepository.findByIdAndUserId(id, authenticatedUserProvider.getAuthenticatedUser().getId()).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        mapper.replaceTaskFromDto(taskRequestPutDto, taskPersisted);
        return mapper.entityToTaskResponse(taskRepository.save(taskPersisted));
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
