package com.example.todo.dto;

import com.example.todo.enums.Priority;
import com.example.todo.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private UUID id;
    private int version;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private UUID createdById;
    private UUID assignedToId;
    private LocalDate dueDate;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
