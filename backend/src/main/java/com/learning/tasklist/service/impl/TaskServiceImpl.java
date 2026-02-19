package com.learning.tasklist.service.impl;

import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.domain.model.TaskPriority;
import com.learning.tasklist.domain.model.TaskStatus;
import com.learning.tasklist.repository.TaskDao;
import com.learning.tasklist.repository.TaskListDao;
import com.learning.tasklist.service.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;
    private final TaskListDao taskListDao;

    public TaskServiceImpl(TaskDao taskDao, TaskListDao taskListDao) {
        this.taskDao = taskDao;
        this.taskListDao = taskListDao;
    }

    @Override
    public List<Task> getTasksByTaskListId(UUID id) {
        return taskDao.findAllTasksByTaskListId(id);
    }

    @Override
    public Optional<Task> getTaskByTaskListIdAndTaskId(UUID id, UUID taskId) {
        return taskDao.findTaskByTaskListIdAndTaskId(id, taskId);
    }

    @Transactional
    @Override
    public Task addTaskByTaskListId(UUID id, Task task) {
        TaskList taskList = taskListDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task list not found!"));

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required!");
        }
        if (task.getDueDate() == null || task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date is invalid!");
        }
        if (task.getTaskPriority() == null) {
            task.setTaskPriority(TaskPriority.MEDIUM);
        }

        LocalDateTime now = LocalDateTime.now();
        return taskDao.save(new Task(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                TaskStatus.OPEN,
                task.getTaskPriority(),
                taskList,
                now,
                now
        ));
    }

    @Transactional
    @Override
    public Task updateTaskByTaskListId(UUID id, UUID taskId, Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required!");
        }
        if (task.getDueDate() == null || task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date is invalid!");
        }

        Task existingTask = taskDao.findTaskByTaskListIdAndTaskId(id, taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found!"));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setTaskStatus(task.getTaskStatus() == null ? existingTask.getTaskStatus() : task.getTaskStatus());
        existingTask.setTaskPriority(task.getTaskPriority() == null ? existingTask.getTaskPriority() : task.getTaskPriority());
        existingTask.setUpdated(LocalDateTime.now());
        return taskDao.save(existingTask);
    }

    @Transactional
    @Override
    public void deleteTaskByTaskListIdAndTaskId(UUID id, UUID taskId) {
        taskDao.deleteTaskByTaskListIdAndTaskId(id, taskId);
    }


}
