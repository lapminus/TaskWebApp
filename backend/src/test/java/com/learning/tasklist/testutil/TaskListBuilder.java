package com.learning.tasklist.testutil;

import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskListBuilder {
    private UUID id = UUID.randomUUID();
    private String title = "Default TaskList";
    private String description = "Default TaskList description";
    private List<Task> tasks = new ArrayList<>();
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime updated = LocalDateTime.now();

    public static TaskListBuilder builder() {
        return new TaskListBuilder();
    }

    public TaskListBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public TaskListBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskListBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskListBuilder tasks(List<Task> tasks) {
        this.tasks = tasks;
        return this;
    }

    public TaskListBuilder created(LocalDateTime created) {
        this.created = created;
        return this;
    }

    public TaskListBuilder updated(LocalDateTime updated) {
        this.updated = updated;
        return this;
    }

    public TaskList build() {
        return new TaskList(
                id,
                title,
                description,
                tasks,
                created,
                updated
        );
    }
}
