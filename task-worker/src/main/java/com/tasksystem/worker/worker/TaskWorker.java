package com.tasksystem.worker.worker;

import com.tasksystem.common.entity.Task;
import com.tasksystem.common.enums.TaskStatus;
import com.tasksystem.worker.repository.TaskRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class TaskWorker {

    private final TaskRepository taskRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public TaskWorker(TaskRepository taskRepository, StringRedisTemplate stringRedisTemplate) {
        this.taskRepository = taskRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @RabbitListener(queues = "task.queue")
    public void processTask(String taskId) throws InterruptedException {

        System.out.println("Worker received task: " + taskId);

        Task task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() != TaskStatus.PENDING) {
            return;
        }

        try {

            task.setStatus(TaskStatus.PROCESSING);
            taskRepository.save(task);

            stringRedisTemplate.opsForHash()
                    .put("task:" + taskId, "status", TaskStatus.PROCESSING.name());

            Thread.sleep(20000);

            task.setStatus(TaskStatus.COMPLETED);

            stringRedisTemplate.opsForHash()
                    .put("task:" + taskId, "status", TaskStatus.COMPLETED.name());

        } catch (Exception e) {

            task.setStatus(TaskStatus.FAILED);

            stringRedisTemplate.opsForHash()
                    .put("task:" + taskId, "status", TaskStatus.FAILED.name());

            throw e;
        }

        taskRepository.save(task);

        System.out.println("Task completed: " + taskId);
    }

    @RabbitListener(queues = "task.dlq")
    public void handleFailedTask(UUID taskId) {

        Task task = taskRepository.findById(taskId).orElseThrow();

        task.setStatus(TaskStatus.FAILED);

        taskRepository.save(task);

    }
}
