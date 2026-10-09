package com.junior.todomanager.controller;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskStatusRequestDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<Task>> findAll(Pageable pageable,
                                              @RequestParam(required = false) TaskStatus status,
                                              @RequestParam(required = false) TaskCategory category,
                                              @RequestParam(required = false) String title,
                                              @RequestParam(required = false) Boolean overDue) {
        return ResponseEntity.ok(service.findAll(pageable, status, category, title, overDue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Task> save(@Valid @RequestBody TaskRequestPostDto taskRequestPostDto) {
        Task task = service.save(taskRequestPostDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(uri).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@Valid @RequestBody TaskRequestPutDto taskRequestPutDto, @PathVariable Long id) {
        Task task = service.update(taskRequestPutDto, id);

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> changeStatus(@PathVariable Long id, @RequestBody TaskStatusRequestDto taskStatusRequestDto) {
        return ResponseEntity.ok(service.changeTaskStatus(id, taskStatusRequestDto));
    }
}
