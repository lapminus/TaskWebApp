package com.learning.tasklist.controller;

import com.learning.tasklist.domain.dto.TaskListDto;
import com.learning.tasklist.domain.model.TaskList;
import com.learning.tasklist.mappers.TaskListMapper;
import com.learning.tasklist.service.TaskListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasklist")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    public TaskListController(TaskListService taskListService, TaskListMapper taskListMapper) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
    }

    @GetMapping
    public ResponseEntity<List<TaskListDto>> getAllTaskLists() {
        List<TaskList> taskLists = taskListService.getAllTaskLists();
        List<TaskListDto> taskListDtos = taskLists.stream()
                .map(taskList -> taskListMapper.toDto(taskList))
                .toList();
        return ResponseEntity.ok(taskListDtos);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskListDto> getTaskListById(@PathVariable UUID id) {
        Optional<TaskList> existingTaskList = taskListService.getTaskListById(id);
        if (existingTaskList.isPresent()) {
            return ResponseEntity.ok(taskListMapper.toDto(existingTaskList.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskListDto> createTaskList(@RequestBody TaskListDto taskListDto) {
        TaskList taskList = taskListMapper.fromDto(taskListDto);
        TaskList createdTaskList = taskListService.createTaskList(taskList);
        return new ResponseEntity<>(taskListMapper.toDto(createdTaskList), HttpStatus.CREATED);
    }


    @PutMapping(path = "/{id}")
    public ResponseEntity<TaskListDto> updateTaskList(
            @PathVariable UUID id, @RequestBody TaskListDto taskListDto) {
        TaskList taskList = taskListService.updateTaskList(id, taskListMapper.fromDto(taskListDto));
        return ResponseEntity.ok(taskListMapper.toDto(taskList));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTaskListById(@PathVariable UUID id) {
        taskListService.deleteTaskListById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
