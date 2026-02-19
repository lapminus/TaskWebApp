package com.learning.tasklist.service;

import com.learning.tasklist.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {
    List<Task> getTasksByTaskListId(UUID id);

    Optional<Task> getTaskByTaskListIdAndTaskId(UUID id, UUID taskId);

    Task addTaskByTaskListId(UUID id, Task task);

    Task updateTaskByTaskListId(UUID id, UUID taskId, Task task);

    void deleteTaskByTaskListIdAndTaskId(UUID id, UUID taskId);
}
