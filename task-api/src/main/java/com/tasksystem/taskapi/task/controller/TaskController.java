package com.tasksystem.taskapi.task.controller;

import com.tasksystem.common.dto.TaskResponse;
import com.tasksystem.taskapi.task.dto.TaskRequest;
import com.tasksystem.taskapi.task.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request.getTitle());
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }
}
