package com.learning.tasklist.controller;

import com.learning.tasklist.domain.dto.TaskListDto;
import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.domain.model.TaskStatus;
import com.learning.tasklist.mappers.impl.TaskListMapperImpl;
import com.learning.tasklist.mappers.impl.TaskMapperImpl;
import com.learning.tasklist.service.impl.TaskListServiceImpl;
import com.learning.tasklist.testutil.TaskBuilder;
import com.learning.tasklist.testutil.TaskListBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskListController.class)
@Import({TaskListMapperImpl.class, TaskMapperImpl.class})
public class TaskListControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TaskListServiceImpl taskListService;

    @Test
    void testGetAllTaskLists() throws Exception {
        Task task1 = TaskBuilder.builder()
                .title("Finish report")
                .status(TaskStatus.CLOSED)
                .build();

        Task task2 = TaskBuilder.builder().build();
        Task task3 = TaskBuilder.builder()
                .title("Buy groceries")
                .build();

        TaskList tl1 = TaskListBuilder.builder()
                .title("Work Tasks")
                .description("Office related tasks")
                .tasks(List.of(task1, task2))
                .build();

        TaskList tl2 = TaskListBuilder.builder()
                .title("Personal")
                .description("Personal todos")
                .tasks(List.of(task3))
                .build();

        List<TaskList> mockTaskLists = List.of(tl1, tl2);
        when(taskListService.getAllTaskLists()).thenReturn(mockTaskLists);

        mockMvc.perform(get("/api/v1/tasklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(tl1.getId().toString()))
                .andExpect(jsonPath("$[0].title").value("Work Tasks"))
                .andExpect(jsonPath("$[0].description").value("Office related tasks"))
                .andExpect(jsonPath("$[0].tasks").isArray())
                .andExpect(jsonPath("$[0].tasks.length()").value(2))
                .andExpect(jsonPath("$[0].tasks[0].title").value("Finish report"))
                .andExpect(jsonPath("$[0].tasks[0].taskStatus").value("CLOSED"))
                .andExpect(jsonPath("$[0].tasks[1].title").value("Default Task"))
                .andExpect(jsonPath("$[0].tasks[1].taskStatus").value("OPEN"))
                .andExpect(jsonPath("$[0].count").value(2))
                .andExpect(jsonPath("$[0].progress").value(50.0))

                .andExpect(jsonPath("$[1].id").value(tl2.getId().toString()))
                .andExpect(jsonPath("$[1].title").value("Personal"))
                .andExpect(jsonPath("$[1].description").value("Personal todos"))
                .andExpect(jsonPath("$[1].tasks").isArray())
                .andExpect(jsonPath("$[1].tasks.length()").value(1))
                .andExpect(jsonPath("$[1].tasks[0].title").value("Buy groceries"))
                .andExpect(jsonPath("$[1].tasks[0].taskStatus").value("OPEN"))
                .andExpect(jsonPath("$[1].count").value(1))
                .andExpect(jsonPath("$[1].progress").value(0.0));
    }

    @Test
    void testGetTaskListByIdReturns200WhenExists() throws Exception {
        UUID taskListId = UUID.randomUUID();

        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .title("Work Tasks")
                .description("Office tasks")
                .tasks(new ArrayList<>())
                .build();

        when(taskListService.getTaskListById(taskListId))
                .thenReturn(Optional.of(taskList));

        mockMvc.perform(get("/api/v1/tasklist/{id}", taskListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskListId.toString()))
                .andExpect(jsonPath("$.title").value("Work Tasks"))
                .andExpect(jsonPath("$.description").value("Office tasks"))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks").isEmpty())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.progress").value(0.0));
    }

    @Test
    void testGetTaskListByIdReturns404WhenNotExists() throws Exception {
        UUID taskListId = UUID.randomUUID();

        when(taskListService.getTaskListById(taskListId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasklist/{id}", taskListId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTaskList() throws Exception {
        TaskListDto inputDto = new TaskListDto(
                null,
                "New Task List",
                "Description for new list",
                null,
                null,
                null
        );

        TaskList createdTaskList = TaskListBuilder.builder()
                .id(UUID.randomUUID())
                .title("New Task List")
                .description("Description for new list")
                .tasks(new ArrayList<>())
                .build();

        when(taskListService.createTaskList(any(TaskList.class)))
                .thenReturn(createdTaskList);

        mockMvc.perform(post("/api/v1/tasklist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdTaskList.getId().toString()))
                .andExpect(jsonPath("$.title").value("New Task List"))
                .andExpect(jsonPath("$.description").value("Description for new list"))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks").isEmpty())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.progress").value(0.0));
    }

    @Test
    void testUpdateTaskList() throws Exception {
        UUID taskListId = UUID.randomUUID();

        TaskListDto inputDto = new TaskListDto(
                taskListId,
                "Updated Title",
                "Updated Description",
                null, null, null
        );

        TaskList updatedTaskList = TaskListBuilder.builder()
                .id(taskListId)
                .title("Updated Title")
                .description("Updated Description")
                .tasks(new ArrayList<>())
                .build();

        when(taskListService.updateTaskList(eq(taskListId), any(TaskList.class)))
                .thenReturn(updatedTaskList);

        mockMvc.perform(put("/api/v1/tasklist/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskListId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks").isEmpty())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.progress").value(0.0));
    }

    @Test
    void testDeleteTaskList() throws Exception {
        UUID taskListId = UUID.randomUUID();

        doNothing().when(taskListService).deleteTaskListById(taskListId);

        mockMvc.perform(delete("/api/v1/tasklist/{id}", taskListId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(taskListService, times(1)).deleteTaskListById(taskListId);
    }
}