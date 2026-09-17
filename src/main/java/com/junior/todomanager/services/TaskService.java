package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.repository.TaskRespository;

import java.util.List;

public class TaskService {
    private final TaskRespository taskRespository;

    public TaskService(TaskRespository taskRespository) {
        this.taskRespository = taskRespository;
    }

    public List<Task> findAll() {
        return taskRespository.findAll();
    }

    public Task findById(Long id) {
        return taskRespository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
    }
}
