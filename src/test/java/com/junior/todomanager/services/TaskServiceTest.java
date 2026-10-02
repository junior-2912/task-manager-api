package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Test
    void shouldThrowExceptionWHenDueDateIsBeforeCreateDate() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDate.parse("2000-01-05"), TaskCategory.PERSONAL);
        TaskRequestPutDto taskRequestPutDto = new TaskRequestPutDto("Tarefa de teste 2", "Fazendo teste com JUnit", LocalDate.parse("2001-01-05"), TaskCategory.PERSONAL);


        assertThrows(DateInvalidException.class, () -> taskService.save(taskRequestPostDto));
        assertThrows(DateInvalidException.class, () -> taskService.update(taskRequestPutDto, 1000L));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTheTaskStatusIsNotFinished() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository);

        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDate.parse("2000-01-05"), TaskCategory.PERSONAL);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(DeleteTaskException.class, () -> taskService.deleteById(1000L));

        verify(taskRepository, never()).deleteById(1000L);
    }

}