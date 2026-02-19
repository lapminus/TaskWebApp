package com.learning.tasklist.domain.dto;

import com.learning.tasklist.domain.model.TaskPriority;
import com.learning.tasklist.domain.model.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String description,
        LocalDate dueDate,
        TaskStatus taskStatus,
        TaskPriority taskPriority
) {
}
