package com.example.todo.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequest {
    private String title;
    private String description;
    private UUID createdById;
    private UUID assignedToId;
    private LocalDate dueDate;
    private List<String> tags;
}
