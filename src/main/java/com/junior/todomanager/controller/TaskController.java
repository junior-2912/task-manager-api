package com.junior.todomanager.controller;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskResponseDto;
import com.junior.todomanager.dto.TaskStatusRequestDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/tasks")
@RestController
public class TaskController {
    private final TaskService service;

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> findAll(Pageable pageable,
                                                         @RequestParam(required = false) TaskStatus status,
                                                         @RequestParam(required = false) TaskCategory category,
                                                         @RequestParam(required = false) String title,
                                                         @RequestParam(required = false) Boolean overdue) {
        return ResponseEntity.ok(service.findAll(pageable, status, category, title, overdue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> save(@Valid @RequestBody TaskRequestPostDto taskRequestPostDto) {
        TaskResponseDto task = service.save(taskRequestPostDto);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> update(@Valid @RequestBody TaskRequestPutDto taskRequestPutDto, @PathVariable Long id) {
        return ResponseEntity.ok(service.update(taskRequestPutDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> changeStatus(@PathVariable Long id, @RequestBody @Valid TaskStatusRequestDto taskStatusRequestDto) {
        return ResponseEntity.ok(service.changeTaskStatus(id, taskStatusRequestDto));
    }
}
