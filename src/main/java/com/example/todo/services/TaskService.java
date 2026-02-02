package com.example.todo.services;
import com.example.todo.dto.*;
import com.example.todo.enums.Priority;
import com.example.todo.enums.Status;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest req);
    TaskResponse getTask(UUID id);
    TaskResponse updateStatus(UUID id, UpdateStatusRequest req);
    TaskResponse assign(UUID id, AssignRequest req);
    TaskResponse updatePriority(UUID id, UpdatePriorityRequest req);
    TaskResponse updateDueDate(UUID id, UpdateDueDateRequest req);
    void addComment(UUID id, CreateCommentRequest req);
    List<TaskVersionResponse> getHistory(UUID id);
}
