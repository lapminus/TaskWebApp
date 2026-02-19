package com.learning.tasklist.service;

import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.domain.model.TaskPriority;
import com.learning.tasklist.domain.model.TaskStatus;
import com.learning.tasklist.repository.TaskDao;
import com.learning.tasklist.repository.TaskListDao;
import com.learning.tasklist.service.impl.TaskServiceImpl;
import com.learning.tasklist.testutil.TaskBuilder;
import com.learning.tasklist.testutil.TaskListBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTestUnitTest {
    @Mock
    private TaskDao taskDao;
    @Mock
    private TaskListDao taskListDao;
    @InjectMocks
    private TaskServiceImpl underTest;

    @Test
    void shouldReturnTasksForValidTaskListId() {
        UUID taskListId = UUID.randomUUID();
        Task task1 = TaskBuilder.builder().title("T1").build();
        Task task2 = TaskBuilder.builder().title("T2").build();
        List<Task> expectedTasks = Arrays.asList(task1, task2);
        when(taskDao.findAllTasksByTaskListId(taskListId)).thenReturn(expectedTasks);

        List<Task> actualTasks = underTest.getTasksByTaskListId(taskListId);

        assertEquals(expectedTasks, actualTasks);
        assertEquals(2, actualTasks.size());
        assertEquals("T2", actualTasks.get(1).getTitle());
        verify(taskDao, times(1)).findAllTasksByTaskListId(taskListId);
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksExist() {
        UUID taskListId = UUID.randomUUID();
        when(taskDao.findAllTasksByTaskListId(taskListId)).thenReturn(List.of());

        List<Task> actualTasks = underTest.getTasksByTaskListId(taskListId);

        assertTrue(actualTasks.isEmpty());
        verify(taskDao, times(1)).findAllTasksByTaskListId(taskListId);
    }

    @Test
    void shouldReturnTaskWhenFound() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Task task = TaskBuilder.builder()
                .id(taskId)
                .build();
        when(taskDao.findTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.of(task));

        Optional<Task> result = underTest.getTaskByTaskListIdAndTaskId(taskListId, taskId);

        assertTrue(result.isPresent());
        assertEquals(task, result.get());
        verify(taskDao, times(1)).findTaskByTaskListIdAndTaskId(taskListId, taskId);
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskDao.findTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.empty());

        Optional<Task> result = underTest.getTaskByTaskListIdAndTaskId(taskListId, taskId);

        assertFalse(result.isPresent());
        verify(taskDao, times(1)).findTaskByTaskListIdAndTaskId(taskListId, taskId);
    }

    @Test
    void shouldAddTaskWithValidData() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .title("Test Task List")
                .build();

        Task task = TaskBuilder.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(1))
                .priority(TaskPriority.HIGH)
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(taskList));
        when(taskDao.save(any(Task.class))).thenReturn(task);

        Task result = underTest.addTaskByTaskListId(taskListId, task);

        assertEquals(TaskStatus.OPEN, result.getTaskStatus());
        assertNotNull(result.getCreated());
        assertNotNull(result.getUpdated());
    }

    @Test
    void shouldThrowExceptionWhenTaskListNotFound() {
        UUID taskListId = UUID.randomUUID();
        Task task = TaskBuilder.builder()
                .title("Test Task")
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTaskByTaskListId(taskListId, task)
        );

        assertEquals("Task list not found!", exception.getMessage());
        verify(taskListDao, times(1)).findById(taskListId);
        verify(taskDao, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        UUID taskListId = UUID.randomUUID();
        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task task = TaskBuilder.builder()
                .title(null)
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(taskList));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTaskByTaskListId(taskListId, task)
        );

        assertEquals("Task title is required!", exception.getMessage());
        verify(taskDao, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        UUID taskListId = UUID.randomUUID();
        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task task = TaskBuilder.builder()
                .title("   ")
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(taskList));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTaskByTaskListId(taskListId, task)
        );

        assertEquals("Task title is required!", exception.getMessage());
        verify(taskDao, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDueDateIsNull() {
        UUID taskListId = UUID.randomUUID();
        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task task = TaskBuilder.builder()
                .dueDate(null)
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(taskList));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTaskByTaskListId(taskListId, task)
        );

        assertEquals("Due date is invalid!", exception.getMessage());
        verify(taskDao, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDueDateIsInPast() {
        UUID taskListId = UUID.randomUUID();
        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task task = TaskBuilder.builder()
                .title("Test Task")
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(taskList));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTaskByTaskListId(taskListId, task)
        );

        assertEquals("Due date is invalid!", exception.getMessage());
        verify(taskDao, never()).save(any());
    }

    @Test
    void shouldSetDefaultPriorityWhenNull() {
        UUID taskListId = UUID.randomUUID();
        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task task = TaskBuilder.builder()
                .title("Test Task")
                .priority(null)
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(taskList));
        when(taskDao.save(any(Task.class))).thenReturn(task);

        underTest.addTaskByTaskListId(taskListId, task);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDao).save(taskCaptor.capture());

        assertEquals(TaskPriority.MEDIUM, taskCaptor.getValue().getTaskPriority());
    }

    @Test
    void shouldUpdateTaskWithValidData() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task existingTask = TaskBuilder.builder()
                .id(taskId)
                .title("Old Title")
                .description("Old Description")
                .dueDate(LocalDate.now().plusDays(2))
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.HIGH)
                .taskList(taskList)
                .build();

        Task updatedTaskData = TaskBuilder.builder()
                .title("Updated Title")
                .description("Updated Description")
                .dueDate(LocalDate.now().plusDays(5))
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.LOW)
                .build();

        when(taskDao.findTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.of(existingTask));
        when(taskDao.save(any(Task.class))).thenReturn(existingTask);

        Task result = underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskStatus.OPEN, result.getTaskStatus());
        assertEquals(TaskPriority.LOW, result.getTaskPriority());
        assertNotNull(result.getUpdated());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTaskNotFound() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task updatedTaskData = TaskBuilder.builder()
                .title("Updated Title")
                .build();

        when(taskDao.findTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData)
        );

        assertEquals("Task not found!", exception.getMessage());
        verify(taskDao, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatedTitleIsNull() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task updatedTaskData = TaskBuilder.builder()
                .title(null)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData)
        );

        assertEquals("Task title is required!", exception.getMessage());
        verify(taskDao, never()).findTaskByTaskListIdAndTaskId(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatedTitleIsBlank() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task updatedTaskData = TaskBuilder.builder()
                .title("   ")
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData)
        );

        assertEquals("Task title is required!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatedDueDateIsNull() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task updatedTaskData = TaskBuilder.builder()
                .title("Updated Title")
                .dueDate(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData)
        );

        assertEquals("Due date is invalid!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatedDueDateIsInPast() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task updatedTaskData = TaskBuilder.builder()
                .title("Updated Title")
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData)
        );

        assertEquals("Due date is invalid!", exception.getMessage());
    }

    @Test
    void shouldKeepExistingStatusWhenUpdatedStatusIsNull() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task existingTask = TaskBuilder.builder()
                .id(taskId)
                .title("Test Task")
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.HIGH)
                .taskList(taskList)
                .build();

        Task updatedTaskData = TaskBuilder.builder()
                .title("Updated Title")
                .dueDate(LocalDate.now().plusDays(1))
                .status(null)
                .build();

        when(taskDao.findTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.of(existingTask));
        when(taskDao.save(any(Task.class))).thenReturn(existingTask);

        underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDao).save(taskCaptor.capture());

        assertEquals(TaskStatus.OPEN, taskCaptor.getValue().getTaskStatus());
    }

    @Test
    void shouldKeepExistingPriorityWhenUpdatedPriorityIsNull() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TaskList taskList = TaskListBuilder.builder()
                .id(taskListId)
                .build();

        Task existingTask = TaskBuilder.builder()
                .id(taskId)
                .title("Test Task")
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.HIGH)
                .taskList(taskList)
                .build();

        Task updatedTaskData = TaskBuilder.builder()
                .title("Updated Title")
                .dueDate(LocalDate.now().plusDays(1))
                .priority(null)
                .build();

        when(taskDao.findTaskByTaskListIdAndTaskId(taskListId, taskId))
                .thenReturn(Optional.of(existingTask));
        when(taskDao.save(any(Task.class))).thenReturn(existingTask);

        underTest.updateTaskByTaskListId(taskListId, taskId, updatedTaskData);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDao).save(taskCaptor.capture());

        assertEquals(TaskPriority.HIGH, taskCaptor.getValue().getTaskPriority());
    }

    @Test
    void shouldDeleteTask() {
        UUID taskListId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        doNothing().when(taskDao).deleteTaskByTaskListIdAndTaskId(taskListId, taskId);

        underTest.deleteTaskByTaskListIdAndTaskId(taskListId, taskId);

        verify(taskDao, times(1)).deleteTaskByTaskListIdAndTaskId(taskListId, taskId);
    }
}
