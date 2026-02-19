package com.learning.tasklist.controller;

import com.learning.tasklist.domain.dto.TaskDto;
import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskPriority;
import com.learning.tasklist.domain.model.TaskStatus;
import com.learning.tasklist.mappers.impl.TaskMapperImpl;
import com.learning.tasklist.service.impl.TaskServiceImpl;
import com.learning.tasklist.testutil.TaskBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(TaskMapperImpl.class)
public class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TaskServiceImpl taskService;

    @Test
    void testThatGetTasksByTaskListId() throws Exception {
        Task task1 = TaskBuilder.builder()
                .title("Finish report")
                .status(TaskStatus.CLOSED)
                .build();

        Task task2 = TaskBuilder.builder().build();
        Task task3 = TaskBuilder.builder()
                .title("Buy groceries")
                .build();

        List<Task> mockTasks = List.of(task1, task2, task3);

        when(taskService.getTasksByTaskListId(any(UUID.class)))
                .thenReturn(mockTasks);

        UUID taskListId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/tasklist/{task_list_id}/tasks", taskListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Finish report"))
                .andExpect(jsonPath("$[0].taskStatus").value("CLOSED"))
                .andExpect(jsonPath("$[1].title").value("Default Task"))
                .andExpect(jsonPath("$[1].taskStatus").value("OPEN"))
                .andExpect(jsonPath("$[2].title").value("Buy groceries"))
                .andExpect(jsonPath("$[2].taskStatus").value("OPEN"));
    }

    @Test
    void testGetTaskByTaskListIdAndTaskIdReturns200WhenExists() throws Exception {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = TaskBuilder.builder()
                .id(taskId)
                .title("Important Task")
                .description("Must complete today")
                .status(TaskStatus.CLOSED)
                .build();

        when(taskService.getTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/v1/tasklist/{task_list_id}/tasks/{task_id}",
                        taskListId, taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Important Task"))
                .andExpect(jsonPath("$.description").value("Must complete today"))
                .andExpect(jsonPath("$.taskStatus").value("CLOSED"))
                .andExpect(jsonPath("$.taskPriority").value("MEDIUM"));
    }

    @Test
    void testGetTaskByTaskListIdAndTaskIdReturns404WhenNotFound() throws Exception {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        when(taskService.getTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasklist/{task_list_id}/tasks/{task_id}",
                        taskListId, taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddingATaskToATaskList() throws Exception {
        UUID taskListId = UUID.randomUUID();

        TaskDto requestDto = new TaskDto(
                null,
                "New Task",
                "Task description",
                LocalDate.now().plusDays(7),
                TaskStatus.OPEN,
                TaskPriority.HIGH
        );

        Task createdTask = TaskBuilder.builder()
                .id(UUID.randomUUID())
                .title("New Task")
                .description("Task description")
                .dueDate(LocalDate.now().plusDays(7))
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.addTaskByTaskListId(eq(taskListId), any(Task.class)))
                .thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/tasklist/{task_list_id}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdTask.getId().toString()))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.taskStatus").value("OPEN"))
                .andExpect(jsonPath("$.taskPriority").value("HIGH"));
    }

    @Test
    void testUpdatingATaskInATaskList() throws Exception {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TaskDto updateDto = new TaskDto(
                taskId,
                "Updated Task Title",
                "Updated description",
                LocalDate.now().plusDays(14),
                TaskStatus.CLOSED,
                TaskPriority.LOW
        );

        Task updatedTask = TaskBuilder.builder()
                .id(taskId)
                .title("Updated Task Title")
                .description("Updated description")
                .dueDate(LocalDate.now().plusDays(14))
                .status(TaskStatus.CLOSED)
                .priority(TaskPriority.LOW)
                .build();

        when(taskService.updateTaskByTaskListId(eq(taskListId), eq(taskId), any(Task.class)))
                .thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasklist/{task_list_id}/tasks/{task_id}",
                        taskListId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Task Title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.taskStatus").value("CLOSED"))
                .andExpect(jsonPath("$.taskPriority").value("LOW"));
    }

    @Test
    void testDeleteTaskByTaskListIdAndTaskId() throws Exception {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        doNothing().when(taskService).deleteTaskByTaskListIdAndTaskId(taskListId, taskId);

        mockMvc.perform(delete("/api/v1/tasklist/{task_list_id}/tasks/{task_id}",
                        taskListId, taskId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(taskService, times(1)).deleteTaskByTaskListIdAndTaskId(taskListId, taskId);
    }
}