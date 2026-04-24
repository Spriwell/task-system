package com.tasksystem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private String title;
    private String status;
}
