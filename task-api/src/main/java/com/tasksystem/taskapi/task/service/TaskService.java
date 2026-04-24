package com.tasksystem.taskapi.task.service;

import com.tasksystem.common.entity.Task;
import com.tasksystem.common.dto.TaskResponse;
import com.tasksystem.common.enums.TaskStatus;
import com.tasksystem.taskapi.task.repository.TaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;

    public TaskService(TaskRepository taskRepository, RabbitTemplate rabbitTemplate, StringRedisTemplate redisTemplate) {
        this.taskRepository = taskRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
    }

    public TaskResponse createTask(String title){

        Task task = Task.builder()
                .title(
                        title)
                .status(
                        TaskStatus.PENDING)
                .createdAt(
                        LocalDateTime.now())
                .build();

        Task savedTask = taskRepository.save(task);

        redisTemplate.opsForHash().put("task:" + savedTask.getId(), "id", savedTask.getId().toString());
        redisTemplate.opsForHash().put("task:" + savedTask.getId(), "title", savedTask.getTitle());
        redisTemplate.opsForHash().put("task:" + savedTask.getId(), "status", savedTask.getStatus().name());

        redisTemplate.expire("task:" + savedTask.getId(), 30, TimeUnit.MINUTES);

        rabbitTemplate.convertAndSend("task.queue", savedTask.getId().toString());

        return new TaskResponse(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getStatus().name()
        );
    }

    public TaskResponse getTaskById(UUID id) {

        String key = "task:" + id;
        Map<Object, Object> cached = redisTemplate.opsForHash().entries(key);

        if (!cached.isEmpty()) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
            return new TaskResponse(
                    UUID.fromString((String) cached.get("id")),
                    (String) cached.get("title"),
                    (String) cached.get("status")
            );
        }

        Task task = taskRepository.findById(id).orElseThrow();

        redisTemplate.opsForHash().put(key, "id",     task.getId().toString());
        redisTemplate.opsForHash().put(key, "title",  task.getTitle());
        redisTemplate.opsForHash().put(key, "status", task.getStatus().name());
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getStatus().name()
        );
    }
}
