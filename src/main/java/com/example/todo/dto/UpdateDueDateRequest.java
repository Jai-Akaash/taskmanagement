package com.example.todo.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDueDateRequest {
    private LocalDate dueDate;
    private UUID performedById;
}
