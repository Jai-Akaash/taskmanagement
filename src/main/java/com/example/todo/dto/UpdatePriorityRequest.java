package com.example.todo.dto;

import com.example.todo.enums.Priority;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePriorityRequest {
    private Priority priority;
    private UUID performedById;
}