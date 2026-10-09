package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.TaskAlreadyFinishedException;
import com.junior.todomanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    @Test
    void shouldThrowExceptionWhenCannotFoundTaskById() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(999L));
    }

    @Test
    void shouldThrowExceptionWHenDueDateIsBeforeCreateDate() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        TaskRequestPutDto taskRequestPutDto = new TaskRequestPutDto("Tarefa de teste 2", "Fazendo teste com JUnit", LocalDateTime.parse("2001-01-05T12:00:00"), TaskCategory.PERSONAL);


        assertThrows(DateInvalidException.class, () -> taskService.save(taskRequestPostDto));
        assertThrows(DateInvalidException.class, () -> taskService.update(taskRequestPutDto, 1000L));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingActiveTask() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(DeleteTaskException.class, () -> taskService.deleteById(1000L));

        verify(taskRepository, never()).deleteById(1000L);
    }

    @Test
    void shouldThrowExceptionInFinishMethodWhenTaskAlreadyFinished() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        task.setId(1000L);
        task.setTaskStatus(TaskStatus.FINISHED);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(TaskAlreadyFinishedException.class, () -> taskService.finishTask(1000L));

        verify(taskRepository, never()).save(task);
    }

    @Test
    void shouldThrowExceptionInChangeMethodWhenTaskAlreadyFinished() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        task.setId(1000L);
        task.setTaskStatus(TaskStatus.FINISHED);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(TaskAlreadyFinishedException.class, () -> taskService.changeTaskStatus(1000L, null));

        verify(taskRepository, never()).save(task);
    }

    @Test
    void shouldFinishTaskSuccessfully() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        taskService.finishTask(1000L);
        assertEquals(TaskStatus.FINISHED, task.getTaskStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void shouldSaveAValidTask() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto("Tarefa de teste 1",
                "Fazendo teste com JUnit",
                LocalDateTime.parse("3000-01-05T12:00:00"),
                TaskCategory.PERSONAL);

        taskService.save(taskRequestPostDto);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(captor.capture());

        Task taskCapted = captor.getValue();

        assertEquals("Tarefa de teste 1", taskCapted.getTitle());
        assertEquals("Fazendo teste com JUnit", taskCapted.getDescription());
        assertEquals(LocalDateTime.parse("3000-01-05T12:00:00"), taskCapted.getDueDate());
        assertEquals(TaskCategory.PERSONAL, taskCapted.getTaskCategory());
    }
}