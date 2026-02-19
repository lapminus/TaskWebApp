package com.learning.tasklist.mappers;

import com.learning.tasklist.domain.dto.TaskDto;
import com.learning.tasklist.domain.model.Task;

public interface TaskMapper {
    Task fromDto(TaskDto taskDto);
    TaskDto toDto(Task task);
}
