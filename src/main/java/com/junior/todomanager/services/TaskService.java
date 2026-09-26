package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
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
    public Task save(TaskRequestPostDto taskRequestPostDto) {
        if (taskRequestPostDto.getDueDate() != null && taskRequestPostDto.getDueDate().isBefore(LocalDate.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = Task.builder()
                .title(taskRequestPostDto.getTitle())
                .description(taskRequestPostDto.getDescription())
                .taskCategory(taskRequestPostDto.getTaskCategory())
                .taskStatus(taskRequestPostDto.getTaskStatus())
                .dueDate(taskRequestPostDto.getDueDate())
                .build();

        return taskRepository.save(task);
    }

    @Transactional
    public Task update(TaskRequestPutDto taskRequestPutDto, Long id) {
        if (taskRequestPutDto.getDueDate() != null && taskRequestPutDto.getDueDate().isBefore(LocalDate.now())) {
            throw new DateInvalidException("Due date cannot be earlier than today");
        }
        Task task = findById(id);
        task.setTitle(taskRequestPutDto.getTitle());
        task.setDescription(taskRequestPutDto.getDescription());
        task.setTaskCategory(taskRequestPutDto.getTaskCategory());
        task.setTaskStatus(taskRequestPutDto.getTaskStatus());
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
}
