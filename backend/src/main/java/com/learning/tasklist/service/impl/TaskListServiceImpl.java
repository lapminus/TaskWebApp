package com.learning.tasklist.service.impl;

import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.repository.TaskListDao;
import com.learning.tasklist.service.TaskListService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskListServiceImpl implements TaskListService {

    private final TaskListDao taskListDao;

    public TaskListServiceImpl(TaskListDao taskListDao) {
        this.taskListDao = taskListDao;
    }

    @Override
    public List<TaskList> getAllTaskLists() {
        return taskListDao.findAll();
    }

    @Override
    public TaskList createTaskList(TaskList taskList) {
        if (taskList.getId() != null) {
            throw new IllegalArgumentException("Task list already exists!");
        }
        if (taskList.getTitle() == null || taskList.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task list title is required!");
        }
        LocalDateTime now = LocalDateTime.now();

        return taskListDao.save(new TaskList(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getDescription(),
                null,
                now,
                now
        ));
    }

    @Override
    public Optional<TaskList> getTaskListById(UUID id) {
        return taskListDao.findById(id);
    }

    @Transactional
    @Override
    public TaskList updateTaskList(UUID id, TaskList taskList) {
        TaskList existingTaskList = taskListDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task list not found."));

        if (taskList.getTitle() == null || taskList.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task list title is required.");
        }

        existingTaskList.setTitle(taskList.getTitle());
        existingTaskList.setDescription(taskList.getDescription());
        existingTaskList.setUpdated(LocalDateTime.now());
        return taskListDao.save(existingTaskList);
    }

    @Override
    public void deleteTaskListById(UUID id) {
        taskListDao.deleteById(id);
    }


}
