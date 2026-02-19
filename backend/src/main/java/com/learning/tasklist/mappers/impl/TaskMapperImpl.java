package com.learning.tasklist.mappers.impl;

import com.learning.tasklist.domain.dto.TaskDto;
import com.learning.tasklist.mappers.TaskMapper;
import com.learning.tasklist.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {
    @Override
    public Task fromDto(TaskDto taskDto) {
        return new Task(
                taskDto.id(),
                taskDto.title(),
                taskDto.description(),
                taskDto.dueDate(),
                taskDto.taskStatus(),
                taskDto.taskPriority(),
                null,
                null,
                null
        );
    }

    @Override
    public TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getTaskStatus(),
                task.getTaskPriority()
        );
    }
}
