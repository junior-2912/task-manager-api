package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestDto;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
    }

    @Transactional
    public Task save(TaskRequestDto taskRequestDto) {
        if (taskRequestDto.getDueDate().isBefore(LocalDate.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = Task.builder()
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .taskCategory(taskRequestDto.getTaskCategory())
                .taskStatus(taskRequestDto.getTaskStatus())
                .dueDate(taskRequestDto.getDueDate())
                .build();

        return taskRepository.save(task);
    }
}
