package com.learning.tasklist.repository;

import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.testutil.TaskBuilder;
import com.learning.tasklist.testutil.TaskListBuilder;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskListDaoIntegrationTest {

    @Autowired
    private TaskListDao taskListDao;
    @Autowired
    private TaskDao taskDao;

    @Test
    void testThatDeletingTaskListDeletesTasks() {
        TaskList taskList = TaskListBuilder.builder().id(null).build();

        Task task1 = TaskBuilder.builder().id(null).taskList(taskList).build();
        Task task2 = TaskBuilder.builder().id(null).taskList(taskList).build();

        taskList.setTasks(List.of(task1, task2));
        taskListDao.save(taskList);

        List<Task> tasksBeforeDelete = taskDao.findAll();
        assertEquals(2, tasksBeforeDelete.size());

        taskListDao.delete(taskList);
        List<Task> tasksAfterDelete = taskDao.findAll();
        assertTrue(tasksAfterDelete.isEmpty());
    }

    @Test
    void testThatDeletingTaskDoesNotDeleteTaskList() {
        TaskList taskList = TaskListBuilder.builder().id(null).build();
        Task task = TaskBuilder.builder().id(null).taskList(taskList).build();

        taskList.setTasks(List.of(task));
        taskListDao.save(taskList);

        taskDao.delete(task);

        assertTrue(taskListDao.findById(taskList.getId()).isPresent());
    }

    @Test
    void testThatTaskListCreatedWithCorrectValues() {
        TaskList taskList = TaskListBuilder.builder()
                .id(null)
                .title("Real TaskList")
                .description("Real TaskList description")
                .build();

        Task task1 = TaskBuilder.builder()
                .id(null)
                .title("Task1")
                .description(null)
                .taskList(taskList)
                .dueDate(LocalDate.parse("2020-04-20"))
                .build();

        Task task2 = TaskBuilder.builder()
                .id(null)
                .title("Task2")
                .description("I have a description")
                .taskList(taskList)
                .dueDate(LocalDate.parse("2026-06-09"))
                .build();

        taskList.setTasks(List.of(task1, task2));
        taskListDao.save(taskList);

        TaskList persisted = taskListDao.findById(taskList.getId())
                .orElseThrow(() -> new AssertionFailure("TaskList not found in DB"));

        assertEquals("Real TaskList", persisted.getTitle());
        assertEquals("Real TaskList description", persisted.getDescription());

        List<Task> persistedTasks = persisted.getTasks();
        assertEquals(2, persistedTasks.size());

        Task t1 = persistedTasks.getFirst();
        assertEquals("Task1", t1.getTitle());
        assertNull(t1.getDescription());
        assertEquals(LocalDate.parse("2020-04-20"), t1.getDueDate());
        assertEquals(taskList, t1.getTaskList());

        Task t2 = persistedTasks.get(1);
        assertEquals("Task2", t2.getTitle());
        assertEquals("I have a description", t2.getDescription());
        assertEquals(LocalDate.parse("2026-06-09"), t2.getDueDate());
        assertEquals(taskList, t2.getTaskList());

    }

}
