package com.junior.todomanager.controller;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskResponseDto;
import com.junior.todomanager.dto.TaskStatusRequestDto;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import com.junior.todomanager.exceptions.DeleteTaskException;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import com.junior.todomanager.exceptions.TaskAlreadyFinishedException;
import com.junior.todomanager.mapper.TaskMapper;
import com.junior.todomanager.mapper.TaskMapperImpl;
import com.junior.todomanager.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TaskController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TaskMapper.class)
})
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper mapper;

    @Test
    void findAll_shouldReturn200WhenSuccessfully() throws Exception {
        PageImpl<TaskResponseDto> taskPage = new PageImpl<>(List.of(createValidTask()).stream().map(mapper::entityToTaskResponse).toList());

        when(taskService.findAll(any(Pageable.class), any(), any(), any(), any())).thenReturn(taskPage);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    void findById_shouldReturnATaskWhenSuccessfully() throws Exception {
        Task task = createValidTask();

        when(taskService.findById(1L)).thenReturn(mapper.entityToTaskResponse(task));

        mockMvc.perform(get("/tasks/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void findById_shouldReturn404WhenIdNotFound() throws Exception {
        when(taskService.findById(2L)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/tasks/{id}", 2))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void save_mustSaveAValidTaskWhenSuccessfully() throws Exception {
        Task task = createTaskToBeSaved();
        task.setId(1L);

        when(taskService.save(any(TaskRequestPostDto.class))).thenReturn(mapper.entityToTaskResponse(task));

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto(task.getTitle(), task.getDescription(), task.getDueDate(), task.getTaskCategory());
        String json = objectMapper.writeValueAsString(taskRequestPostDto);

        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.category").exists());
    }

    @Test
    void save_mustThrowExceptionWhenTaskIsInvalid() throws Exception {
        Task task = createTaskToBeSaved();
        task.setId(1L);

        when(taskService.save(any(TaskRequestPostDto.class))).thenReturn(mapper.entityToTaskResponse(task));

        TaskRequestPostDto taskRequestPostDto = new TaskRequestPostDto(null, task.getDescription(), task.getDueDate(), task.getTaskCategory());
        String json = objectMapper.writeValueAsString(taskRequestPostDto);

        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.category").doesNotExist());
    }

    @Test
    void update_mustUpdateATaskWhenExists() throws Exception {
        Task task = createValidTask();


        when(taskService.update(any(TaskRequestPutDto.class), eq(1L))).thenReturn(mapper.entityToTaskResponse(task));

        TaskRequestPutDto taskRequestPutDto = new TaskRequestPutDto("Tarefa de teste Update", task.getDescription(), task.getDueDate(), task.getTaskCategory());
        String json = objectMapper.writeValueAsString(taskRequestPutDto);

        mockMvc.perform(put("/tasks/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    void update_mustThrowExceptionWhenIdDoesNotExists() throws Exception {
        Task task = createValidTask();

        when(taskService.update(any(TaskRequestPutDto.class), eq(1L))).thenThrow(ResourceNotFoundException.class);

        TaskRequestPutDto taskRequestPutDto = new TaskRequestPutDto("Tarefa de teste Update", task.getDescription(), task.getDueDate(), task.getTaskCategory());
        String json = objectMapper.writeValueAsString(taskRequestPutDto);

        mockMvc.perform(put("/tasks/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void deleteById_mustReturnNoContentWhenSuccessfully() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_mustThrowExceptionWhenTaskIsPendingOrInProgress() throws Exception {
        doThrow(DeleteTaskException.class)
                .when(taskService).deleteById(1L);

        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isConflict());
    }


    @Test
    void changeStatus_mustChangeTheStatusWhenSuccessfully() throws Exception {
        Task task = createValidTask();
        task.setTaskStatus(TaskStatus.FINISHED);


        when(taskService.changeTaskStatus(eq(1L), any(TaskStatusRequestDto.class))).thenReturn(mapper.entityToTaskResponse(task));

        TaskStatusRequestDto taskStatusRequestDto = new TaskStatusRequestDto(TaskStatus.FINISHED);
        String json = objectMapper.writeValueAsString(taskStatusRequestDto);

        mockMvc.perform(patch("/tasks/{id}/status", 1).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }

    @Test
    void changeStatus_mustThrowExceptionWhenTaskIsAlreadyFinished() throws Exception {
        Task task = createValidTask();
        task.setTaskStatus(TaskStatus.FINISHED);


        when(taskService.changeTaskStatus(eq(1L), any(TaskStatusRequestDto.class))).thenThrow(TaskAlreadyFinishedException.class);

        TaskStatusRequestDto taskStatusRequestDto = new TaskStatusRequestDto(TaskStatus.FINISHED);
        String json = objectMapper.writeValueAsString(taskStatusRequestDto);

        mockMvc.perform(patch("/tasks/{id}/status", 1).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    private static Task createValidTask() {
        return new Task(1L, "Tarefa de teste 1",
                "Fazendo teste com JUnit",
                TaskStatus.PENDING,
                LocalDateTime.parse("2027-01-05T12:00:00"),
                TaskCategory.PERSONAL);
    }

    private static Task createTaskToBeSaved() {
        return new Task("Tarefa de teste 1",
                "Fazendo teste com JUnit",
                LocalDateTime.parse("2027-01-05T12:00:00"),
                TaskCategory.PERSONAL);
    }
}