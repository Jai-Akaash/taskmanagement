package com.example.todo.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRequest {
    private UUID assignedToId; // null to unassign
    private UUID performedById;
}