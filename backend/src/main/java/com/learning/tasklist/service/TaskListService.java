package com.learning.tasklist.service;

import com.learning.tasklist.domain.model.TaskList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListService {

    List<TaskList> getAllTaskLists();

    Optional<TaskList> getTaskListById(UUID id);

    TaskList createTaskList(TaskList taskList);

    TaskList updateTaskList(UUID id, TaskList taskList);

    void deleteTaskListById(UUID id);
}
