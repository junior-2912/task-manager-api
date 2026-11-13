package com.junior.todomanager.services;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.domain.User;
import com.junior.todomanager.dto.request.TaskRequestPostDto;
import com.junior.todomanager.dto.request.TaskRequestPatchDto;
import com.junior.todomanager.dto.request.TaskRequestPutDto;
import com.junior.todomanager.dto.request.TaskStatusRequestDto;
import com.junior.todomanager.dto.response.TaskResponseDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DateInvalidException;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.TaskAlreadyFinishedException;
import com.junior.todomanager.mapper.TaskMapper;
import com.junior.todomanager.repository.TaskRepository;
import com.junior.todomanager.util.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskMapper mapper = new TaskMapper() {
        @Override
        public Task requestPostToTaskEntity(TaskRequestPostDto taskRequestPostDto, User user) {
            return null;
        }

        @Override
        public TaskResponseDto entityToTaskResponse(Task task) {
            return null;
        }

        @Override
        public void updateTaskFromDto(TaskRequestPatchDto taskRequestPatchDto, Task entity) {

        }

        @Override
        public void replaceTaskFromDto(TaskRequestPutDto taskRequestPutDto, Task entity) {

        }
    };

    private AuthenticatedUserProvider authenticatedUserProvider =  new AuthenticatedUserProvider();
    @Test
    void findById_shouldThrowExceptionWhenCannotFoundTaskById() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(1L, "Usuario de teste", "teste@email.com", "secreta");
        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(999L));
    }

    @Test
    void saveOrUpdate_shouldThrowExceptionWHenDueDateIsBeforeCreateDate() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        TaskRequestPatchDto taskRequestPatchDto = new TaskRequestPatchDto("Tarefa de teste 2", "Fazendo teste com JUnit", LocalDateTime.parse("2001-01-05T12:00:00"), TaskCategory.PERSONAL);


        assertThrows(DateInvalidException.class, () -> taskService.save(taskRequestPostDto));
        assertThrows(DateInvalidException.class, () -> taskService.patch(taskRequestPatchDto, 1000L));

        verify(taskRepository, never()).save(any());
    }
    @Test
    void update_shouldThrowExceptionWHenDueDateIsBeforeCreateDate() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        TaskRequestPatchDto taskRequestPatchDto = new TaskRequestPatchDto("Tarefa de teste 2", "Fazendo teste com JUnit", LocalDateTime.parse("2001-01-05T12:00:00"), TaskCategory.PERSONAL);


        assertThrows(DateInvalidException.class, () -> taskService.patch(taskRequestPatchDto, 1000L));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldThrowExceptionWhenDeletingActiveTask() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(1L, "Usuario de teste", "teste@email.com", "secreta");
        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(DeleteTaskException.class, () -> taskService.deleteById(1000L));

        verify(taskRepository, never()).deleteById(1000L);
    }


    @Test
    void changeTaskStatus_shouldThrowExceptionWhenTaskAlreadyFinished() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(1L, "Usuario de teste", "teste@email.com", "secreta");
        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);
        task.setTaskStatus(TaskStatus.FINISHED);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        assertThrows(TaskAlreadyFinishedException.class, () -> taskService.changeTaskStatus(1000L, null));

        verify(taskRepository, never()).save(task);
    }

    @Test
    void changeStatus_shouldFinishTaskSuccessfully() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(1L, "Usuario de teste", "teste@email.com", "secreta");
        Task task = new Task("Tarefa de teste 1", "Fazendo teste com JUnit", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);

        when(taskRepository.findById(1000L)).thenReturn(Optional.of(task));

        taskService.changeTaskStatus(1000L, new TaskStatusRequestDto(TaskStatus.FINISHED));
        assertEquals(TaskStatus.FINISHED, task.getTaskStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void save_shouldSaveAValidTask() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

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