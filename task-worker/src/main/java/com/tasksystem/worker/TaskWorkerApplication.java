package com.tasksystem.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.tasksystem.common.entity")
@EnableJpaRepositories(basePackages = "com.tasksystem.worker.repository")
public class TaskWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskWorkerApplication.class, args);
	}

}
