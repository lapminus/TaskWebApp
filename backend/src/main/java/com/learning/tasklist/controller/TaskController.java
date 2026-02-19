package com.learning.tasklist.controller;

import com.learning.tasklist.domain.dto.TaskDto;
import com.learning.tasklist.domain.model.Task;
import com.learning.tasklist.mappers.impl.TaskMapperImpl;
import com.learning.tasklist.service.impl.TaskServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasklist/{task_list_id}/tasks")
public class TaskController {

    private final TaskServiceImpl taskService;
    private final TaskMapperImpl taskMapper;

    public TaskController(TaskServiceImpl taskService, TaskMapperImpl taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByTaskListId(
            @PathVariable(name = "task_list_id") UUID id) {
        List<Task> tasks = taskService.getTasksByTaskListId(id);
        List<TaskDto> taskDtos = tasks.stream()
                .map(task -> taskMapper.toDto(task))
                .toList();
        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping(path = "/{task_id}")
    public ResponseEntity<TaskDto> getTaskByTaskListIdAndTaskId(
            @PathVariable(name = "task_list_id") UUID id,
            @PathVariable(name = "task_id") UUID taskId) {
        Optional<Task> task = taskService.getTaskByTaskListIdAndTaskId(id, taskId);
        if (task.isPresent()) {
            return ResponseEntity.ok(taskMapper.toDto(task.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskDto> addTaskByTaskListId(
            @PathVariable(name = "task_list_id") UUID id,
            @RequestBody TaskDto taskDto) {
        Task task = taskService.addTaskByTaskListId(id, taskMapper.fromDto(taskDto));
        return new ResponseEntity<>(taskMapper.toDto(task), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{task_id}")
    public ResponseEntity<TaskDto> updateTaskByTaskListId(
            @PathVariable(name = "task_list_id") UUID id,
            @PathVariable(name = "task_id") UUID taskId,
            @RequestBody TaskDto taskDto) {

        Task updatedTask = taskService.updateTaskByTaskListId(id, taskId, taskMapper.fromDto(taskDto));
        return new ResponseEntity<>(taskMapper.toDto(updatedTask), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{task_id}")
    public ResponseEntity<Void> deleteTaskByTaskListIdAndTaskId(
            @PathVariable(name = "task_list_id") UUID id,
            @PathVariable(name = "task_id") UUID taskId) {

        taskService.deleteTaskByTaskListIdAndTaskId(id, taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
