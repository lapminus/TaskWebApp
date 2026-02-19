package com.learning.tasklist.mappers.impl;

import com.learning.tasklist.domain.dto.TaskListDto;
import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.domain.model.TaskStatus;
import com.learning.tasklist.mappers.TaskListMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class TaskListMapperImpl implements TaskListMapper {

    private final TaskMapperImpl taskMapper;

    public TaskListMapperImpl(TaskMapperImpl taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskList fromDto(TaskListDto taskListDto) {
        return new TaskList(
                taskListDto.id(),
                taskListDto.title(),
                taskListDto.description(),
                Optional.ofNullable(taskListDto.tasks())
                        .map(tasks -> tasks.stream()
                                .map(task -> taskMapper.fromDto(task))
                                .toList())
                        .orElse(null),
                null,
                null
        );
    }

    @Override
    public TaskListDto toDto(TaskList taskList) {
        return new TaskListDto(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getDescription(),
                Optional.ofNullable(taskList.getTasks())
                        .map(tasks -> tasks.stream()
                                .map(task -> taskMapper.toDto(task))
                                .toList())
                        .orElse(null),
                Optional.ofNullable(taskList.getTasks())
                        .map(tasks -> tasks.size())
                        .orElse(0),
                progressOfTaskList(taskList.getTasks())
        );
    }

    private Double progressOfTaskList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return 0.0;
        }

        long countOfClosedTasks = tasks.stream()
                .filter(task -> task.getTaskStatus() == TaskStatus.CLOSED)
                .count();

        return (double) countOfClosedTasks / tasks.size() * 100;

    }
}
