package com.example.todo.controllers;

import com.example.todo.dto.*;
import com.example.todo.enums.Priority;
import com.example.todo.enums.Status;
import com.example.todo.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody CreateTaskRequest req) {
        return ResponseEntity.ok(taskService.createTask(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable UUID id, @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(taskService.updateStatus(id, req));
    }

    @PatchMapping("/{id}/assignee")
    public ResponseEntity<TaskResponse> assign(@PathVariable UUID id, @RequestBody AssignRequest req) {
        return ResponseEntity.ok(taskService.assign(id, req));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskResponse> updatePriority(@PathVariable UUID id, @RequestBody UpdatePriorityRequest req) {
        return ResponseEntity.ok(taskService.updatePriority(id, req));
    }

    @PatchMapping("/{id}/due-date")
    public ResponseEntity<TaskResponse> updateDueDate(@PathVariable UUID id, @RequestBody UpdateDueDateRequest req) {
        return ResponseEntity.ok(taskService.updateDueDate(id, req));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> addComment(@PathVariable UUID id, @RequestBody CreateCommentRequest req) {
        taskService.addComment(id, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskVersionResponse>> history(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getHistory(id));
    }
    @GetMapping("/search")
    public Page<TaskResponse> searchTasks(
            @RequestParam(required = false) List<Status> status,
            @RequestParam(required = false) List<Priority> priority,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Boolean overdue,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedTo,

            @RequestParam(required = false) List<String> tags,
            Pageable pageable
    ) {
        return taskService.searchTasks(
                status, priority, assigneeId, creatorId, overdue,
                createdFrom, createdTo, updatedFrom, updatedTo,
                completedFrom, completedTo, tags, pageable
        );
    }

}