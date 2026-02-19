package com.learning.tasklist.service;

import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.repository.TaskListDao;
import com.learning.tasklist.service.impl.TaskListServiceImpl;
import com.learning.tasklist.testutil.TaskListBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskListServiceImplUnitTest {
    @Mock
    private TaskListDao taskListDao;
    @InjectMocks
    private TaskListServiceImpl underTest;

    @Test
    void shouldReturnTaskLists() {
        TaskList taskList1 = TaskListBuilder.builder().build();
        TaskList taskList2 = TaskListBuilder.builder().build();
        List<TaskList> expectedTaskLists = List.of(taskList1, taskList2);

        when(taskListDao.findAll()).thenReturn(expectedTaskLists);
        List<TaskList> actualTaskLists = underTest.getAllTaskLists();

        assertEquals(expectedTaskLists, actualTaskLists);
        assertEquals(2, actualTaskLists.size());
        verify(taskListDao, times(1)).findAll();
    }

    @Test
    void shouldReturnTaskList() {
        UUID taskListId = UUID.randomUUID();
        TaskList expected = TaskListBuilder.builder()
                .id(taskListId)
                .title("Meme")
                .build();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.of(expected));

        Optional<TaskList> actual = underTest.getTaskListById(taskListId);

        assertEquals(expected, actual.get());
        assertEquals("Meme", actual.get().getTitle());
        verify(taskListDao, times(1)).findById(taskListId);
    }

    @Test
    void shouldReturnEmptyTaskListWhenTaskListIsNotFound() {
        UUID taskListId = UUID.randomUUID();

        when(taskListDao.findById(taskListId)).thenReturn(Optional.empty());
        Optional<TaskList> actual = underTest.getTaskListById(taskListId);

        assertEquals(Optional.empty(), actual);
        verify(taskListDao, times(1)).findById(taskListId);
    }

    @Test
    void shouldAddTaskListWithValidData() {
        TaskList taskList = TaskListBuilder.builder().id(null).title("Created").build();

        when(taskListDao.save(any(TaskList.class))).thenReturn(taskList);

        TaskList result = underTest.createTaskList(taskList);

        assertEquals(taskList, result);
        assertEquals("Created", result.getTitle());
        assertNotNull(result.getCreated());
        assertNotNull(result.getUpdated());
        verify(taskListDao, times(1)).save(any(TaskList.class));
    }

    @Test
    void shouldThrowExceptionWhenAddTaskListWithId() {
        UUID taskListId = UUID.randomUUID();
        TaskList taskList = TaskListBuilder.builder().id(taskListId).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.createTaskList(taskList));

        assertEquals("Task list already exists!", exception.getMessage());
        verify(taskListDao, never()).save(any(TaskList.class));
    }

    @Test
    void shouldThrowExceptionWhenAddTaskListWithoutTitle() {
        TaskList taskList = TaskListBuilder.builder().id(null).title(null).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.createTaskList(taskList));

        assertEquals("Task list title is required!", exception.getMessage());
        verify(taskListDao, never()).save(any(TaskList.class));
    }

    @Test
    void shouldThrowExceptionWhenAddTaskListWithBlankTitle() {
        TaskList taskList = TaskListBuilder.builder().id(null).title("   ").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.createTaskList(taskList));

        assertEquals("Task list title is required!", exception.getMessage());
        verify(taskListDao, never()).save(any(TaskList.class));
    }

    @Test
    void shouldUpdateTaskListWithValidData() {
        TaskList existing = TaskListBuilder.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now().minusDays(1))
                .updated(LocalDateTime.now().minusDays(1))
                .title("Old")
                .build();
        TaskList updated = TaskListBuilder.builder()
                .id(existing.getId())
                .title("New")
                .created(LocalDateTime.now().minusDays(1))
                .updated(LocalDateTime.now())
                .build();

        when(taskListDao.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(taskListDao.save(any(TaskList.class))).thenReturn(updated);

        TaskList result = underTest.updateTaskList(existing.getId(), updated);
        assertEquals(updated, result);
        assertEquals("New", result.getTitle());
        assertEquals(updated.getCreated(), result.getCreated());
        assertEquals(updated.getUpdated(), result.getUpdated());
        verify(taskListDao, times(1)).findById(existing.getId());
    }

    @Test
    void shouldThrowExceptionWhenUpdateTaskListWithIdNotFound() {
        UUID taskListId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskList(taskListId, new TaskList()));

        assertEquals("Task list not found.", exception.getMessage());
        verify(taskListDao, never()).save(any(TaskList.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedTitleIsNull() {
        UUID taskListId = UUID.randomUUID();

        TaskList updatedTaskList = TaskListBuilder.builder()
                .id(taskListId)
                .title(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskList(taskListId, updatedTaskList)
        );

        assertEquals("Task list not found.", exception.getMessage());
        verify(taskListDao, never()).save(any(TaskList.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedTitleIsBlank() {
        UUID taskListId = UUID.randomUUID();

        TaskList updatedTaskList = TaskListBuilder.builder()
                .id(taskListId)
                .title("   ")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.updateTaskList(taskListId, updatedTaskList)
        );

        assertEquals("Task list not found.", exception.getMessage());
        verify(taskListDao, never()).save(any(TaskList.class));
    }

    @Test
    void shouldDeleteTask() {
        UUID taskListId = UUID.randomUUID();
        doNothing().when(taskListDao).deleteById(taskListId);

        underTest.deleteTaskListById(taskListId);

        verify(taskListDao, times(1)).deleteById(taskListId);
    }
}
