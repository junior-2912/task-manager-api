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
import com.junior.todomanager.mapper.TaskMapperImpl;
import com.junior.todomanager.repository.TaskRepository;
import com.junior.todomanager.util.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    private final TaskMapper mapper = new TaskMapperImpl();

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final AuthenticatedUserProvider authenticatedUserProvider = mock(AuthenticatedUserProvider.class);

    @Test
    void findById_shouldThrowExceptionWhenCannotFoundTaskById() {
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(7L, "Test User", "test@example.com", "password");
        when(taskRepository.findByIdAndUserId(999L, 7L)).thenReturn(Optional.empty());
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(999L));
        verify(taskRepository).findByIdAndUserId(999L, 7L);
    }

    @Test
    void findById_shouldThrowWhenTaskDoesNotBelongToAuthenticatedUser() {
        TaskService service = new TaskService(taskRepository, mapper, authenticatedUserProvider);
        User user = new User(8L, "Second Test User", "test2@example.com", "password2");

        // Simulate the repository finding no task with ID 100 owned by user 8.
        when(taskRepository.findByIdAndUserId(100L, 8L)).thenReturn(Optional.empty());
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        assertThrows(ResourceNotFoundException.class, () -> service.findById(100L));
        // Verify that the service searched using the authenticated user's ID.
        verify(taskRepository).findByIdAndUserId(100L, 8L);
    }

    @Test
    void saveReplaceAndPatch_shouldThrowWhenDueDateIsInThePast() {
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto("Test task 1", "Running JUnit tests", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL);
        TaskRequestPatchDto taskRequestPatchDto = new TaskRequestPatchDto("Test task 2", "Running JUnit tests", LocalDateTime.parse("2001-01-05T12:00:00"), TaskCategory.PERSONAL);
        TaskRequestPutDto taskRequestPutDto = new TaskRequestPutDto("Test task 3", "Running JUnit tests", LocalDateTime.parse("2001-01-05T12:00:00"), TaskCategory.PERSONAL);


        assertThrows(DateInvalidException.class, () -> taskService.save(taskRequestPostDto));
        assertThrows(DateInvalidException.class, () -> taskService.patch(taskRequestPatchDto, 1000L));
        assertThrows(DateInvalidException.class, () -> taskService.replace(taskRequestPutDto, 1000L));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldThrowExceptionWhenDeletingActiveTask() {
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(7L, "Test User", "test@example.com", "password");
        Task task = new Task("Test task 1", "Running JUnit tests", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);

        when(taskRepository.findByIdAndUserId(1000L, 7L)).thenReturn(Optional.of(task));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        assertThrows(DeleteTaskException.class, () -> taskService.deleteById(1000L));

        verify(taskRepository, never()).deleteById(1000L);
    }


    @Test
    void changeTaskStatus_shouldThrowExceptionWhenTaskAlreadyFinished() {
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(7L, "Test User", "test@example.com", "password");
        Task task = new Task("Test task 1", "Running JUnit tests", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);
        task.setTaskStatus(TaskStatus.FINISHED);

        when(taskRepository.findByIdAndUserId(1000L, 7L)).thenReturn(Optional.of(task));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        assertThrows(TaskAlreadyFinishedException.class, () -> taskService.changeTaskStatus(1000L, new TaskStatusRequestDto(TaskStatus.FINISHED)));

        verify(taskRepository, never()).save(task);
    }

    @Test
    void changeTaskStatus_shouldFinishTaskSuccessfully() {
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        User user = new User(7L, "Test User", "test@example.com", "password");
        Task task = new Task("Test task 1", "Running JUnit tests", LocalDateTime.parse("2000-01-05T12:00:00"), TaskCategory.PERSONAL, user);
        task.setId(1000L);

        when(taskRepository.findByIdAndUserId(1000L, 7L)).thenReturn(Optional.of(task));
        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);

        taskService.changeTaskStatus(1000L, new TaskStatusRequestDto(TaskStatus.FINISHED));
        assertEquals(TaskStatus.FINISHED, task.getTaskStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void save_shouldSaveAValidTask() {
        TaskService taskService = new TaskService(taskRepository, mapper, authenticatedUserProvider);

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto("Test task 1",
                "Running JUnit tests",
                LocalDateTime.parse("3000-01-05T12:00:00"),
                TaskCategory.PERSONAL);

        User user = new User(7L, "Test User", "test@example.com", "password");

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDto answer = taskService.save(taskRequestPostDto);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        assertSame(user, captor.getValue().getUser());
        assertEquals("Running JUnit tests", captor.getValue().getDescription());
        assertEquals(LocalDateTime.parse("3000-01-05T12:00:00"), captor.getValue().getDueDate());
        assertEquals(TaskCategory.PERSONAL, captor.getValue().getTaskCategory());
        assertEquals("Test task 1", answer.getTitle());
    }
}
