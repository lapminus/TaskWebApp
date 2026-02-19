package com.learning.tasklist.repository;

import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.testutil.TaskBuilder;
import com.learning.tasklist.testutil.TaskListBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TaskDaoIntegrationTest {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskListDao taskListDao;

    @Test
    void testThatTasksAreReturnedInCreatedOrderByTaskListId() {
        TaskList tl1 = TaskListBuilder.builder().id(null).build();
        TaskList tl2 = TaskListBuilder.builder().id(null).build();

        Task t1 = TaskBuilder.builder()
                .id(null)
                .title("Task1")
                .taskList(tl1)
                .created(LocalDateTime.now().plusDays(1))
                .build();
        Task t2 = TaskBuilder.builder()
                .id(null)
                .title("Task2")
                .taskList(tl1)
                .created(LocalDateTime.now())
                .build();
        Task t3 = TaskBuilder.builder()
                .id(null)
                .title("Task3")
                .taskList(tl2)
                .created(LocalDateTime.now())
                .build();

        taskDao.saveAll(List.of(t1, t2, t3));
        taskListDao.saveAll(List.of(tl1, tl2));

        List<Task> tasks = taskDao.findAllTasksByTaskListId(tl1.getId());

        assertEquals(2, tasks.size());
        assertEquals("Task2", tasks.get(0).getTitle());
        assertEquals("Task1", tasks.get(1).getTitle());
    }

    @Test
    void testThatFindTaskByTaskListIdAndTaskIdReturnsCorrectTask() {
        TaskList tl1 = TaskListBuilder.builder().id(null).build();
        TaskList tl2 = TaskListBuilder.builder().id(null).build();

        Task t1 = TaskBuilder.builder().id(null).taskList(tl1).build();
        Task t2 = TaskBuilder.builder().id(null).taskList(tl1).build();

        taskDao.save(t1);
        taskListDao.saveAll(List.of(tl1, tl2));

        Optional<Task> existingTaskList1 =
                taskDao.findTaskByTaskListIdAndTaskId(tl1.getId(), t1.getId());

        Optional<Task> existingTaskList2 =
                taskDao.findTaskByTaskListIdAndTaskId(tl2.getId(), t2.getId());

        assertTrue(existingTaskList2.isEmpty());
        assertEquals(t1.getId(), existingTaskList1.get().getId());
    }

    @Test
    void testThatDeleteTaskByTaskListIdAndTaskIdDeletesCorrectTask() {
        TaskList tl1 = TaskListBuilder.builder().id(null).build();

        Task t1 = TaskBuilder.builder().id(null).title("Delete").taskList(tl1).build();
        Task t2 = TaskBuilder.builder().id(null).title("Keep").taskList(tl1).build();

        taskDao.saveAll(List.of(t1, t2));
        taskListDao.save(tl1);

        List<Task> tasks = taskDao.findAllTasksByTaskListId(tl1.getId());
        assertEquals(2, tasks.size());

        taskDao.deleteTaskByTaskListIdAndTaskId(tl1.getId(), t1.getId());

        List<Task> remainingTasks = taskDao.findAllTasksByTaskListId(tl1.getId());
        assertEquals(1, remainingTasks.size());
        assertEquals("Keep", remainingTasks.get(0).getTitle());
    }
}
