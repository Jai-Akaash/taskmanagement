package com.example.todo.dto;

import com.example.todo.enums.Status;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequest {
    private Status status;
    private UUID performedById;
}