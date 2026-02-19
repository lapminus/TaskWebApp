package com.learning.tasklist.testutil;

import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.domain.model.TaskPriority;
import com.learning.tasklist.domain.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskBuilder {
    private UUID id = UUID.randomUUID();
    private String title = "Default Task";
    private String description = "Default task description";
    private LocalDate dueDate = LocalDate.now();
    private TaskStatus status = TaskStatus.OPEN;
    private TaskPriority priority = TaskPriority.MEDIUM;
    private TaskList taskList;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime updated = LocalDateTime.now();

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public TaskBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskBuilder priority(TaskPriority priority) {
        this.priority = priority;
        return this;
    }

    public TaskBuilder taskList(TaskList taskList) {
        this.taskList = taskList;
        return this;
    }

    public TaskBuilder created(LocalDateTime created) {
        this.created = created;
        return this;
    }

    public TaskBuilder updated(LocalDateTime updated) {
        this.updated = updated;
        return this;
    }

    public TaskBuilder taskStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
        return this;
    }

    public TaskBuilder taskPriority(TaskPriority taskPriority) {
        this.priority = taskPriority;
        return this;
    }

    public Task build() {
        Task task = new Task(
                id,
                title,
                description,
                dueDate,
                status,
                priority,
                taskList,
                created,
                updated
        );
        if (taskList != null) {
            List<Task> tasks = taskList.getTasks();
            if (tasks != null) {
                tasks.add(task);
            }
        }
        return task;
    }
}
