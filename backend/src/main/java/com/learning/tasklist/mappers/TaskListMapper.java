package com.learning.tasklist.mappers;

import com.learning.tasklist.domain.dto.TaskListDto;
import com.learning.tasklist.domain.model.TaskList;


public interface TaskListMapper {
    TaskList fromDto(TaskListDto taskListDto);
    TaskListDto toDto(TaskList taskList);
}
