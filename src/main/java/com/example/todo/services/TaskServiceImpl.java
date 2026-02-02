package com.example.todo.services;

import com.example.todo.dto.*;
import com.example.todo.enums.ActivityType;
import com.example.todo.enums.Priority;
import com.example.todo.enums.Status;
import com.example.todo.exceptions.InvalidTransitionException;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.models.*;
import com.example.todo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final TaskVersionRepository versionRepo;
    private final CommentRepository commentRepo;
    private final ActivityEventRepository activityRepo;
    private final UserRepository userRepository; // assume exists

    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        ALLOWED_TRANSITIONS.put(Status.OPEN, Set.of(Status.IN_PROGRESS, Status.CANCELLED));
        ALLOWED_TRANSITIONS.put(Status.IN_PROGRESS, Set.of(Status.COMPLETED, Status.CANCELLED));
        ALLOWED_TRANSITIONS.put(Status.CANCELLED, Set.of(Status.OPEN));
        ALLOWED_TRANSITIONS.put(Status.COMPLETED, Set.of());
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest req) {
        // validate
        if (req.getTitle() == null || req.getTitle().isBlank()) throw new IllegalArgumentException("title is required");
        if (req.getDescription() == null || req.getDescription().isBlank())
            throw new IllegalArgumentException("description is required");

        User creator = userRepository.findById(req.getCreatedById())
                .orElseThrow(() -> new NotFoundException("createdBy user not found"));

        User assigned = null;
        if (req.getAssignedToId() != null) {
            assigned = userRepository.findById(req.getAssignedToId())
                    .orElseThrow(() -> new NotFoundException("assignedTo user not found"));
        }

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .version(1)
                .title(req.getTitle())
                .description(req.getDescription())
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .createdBy(creator)
                .assignedTo(assigned)
                .dueDate(req.getDueDate())
                .tags(Optional.ofNullable(req.getTags()).orElseGet(ArrayList::new))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskRepo.save(task);

        // create version snapshot
        TaskVersion version = TaskVersion.builder()
                .id(UUID.randomUUID())
                .task(task)
                .versionNumber(task.getVersion())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assignedTo(task.getAssignedTo())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
        versionRepo.save(version);

        activityRepo.save(ActivityEvent.builder()
                .id(UUID.randomUUID())
                .taskId(task.getId())
                .activityType(ActivityType.TASK_CREATED)
                .performedBy(creator)
                .timestamp(LocalDateTime.now())
                .details("Task created")
                .build());

        return toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(UUID id, UpdateStatusRequest req) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        Status newStatus = req.getStatus();
        if (newStatus == null) throw new IllegalArgumentException("status is required");

        Status current = task.getStatus();
        if (current == newStatus) return toResponse(task);

        Set<Status> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidTransitionException("Invalid status transition: " + current + " -> " + newStatus);
        }

        User performedBy = userRepository.findById(req.getPerformedById()).orElseThrow(() -> new NotFoundException("user not found"));

        // apply
        task.setStatus(newStatus);
        task.setVersion(task.getVersion() + 1);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        saveVersionSnapshot(task);

        activityRepo.save(ActivityEvent.builder()
                .id(UUID.randomUUID())
                .taskId(task.getId())
                .activityType(ActivityType.STATUS_CHANGED)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details("Status changed to " + newStatus)
                .build());

        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse assign(UUID id, AssignRequest req) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));

        User performedBy = userRepository.findById(req.getPerformedById())
                .orElseThrow(() -> new NotFoundException("user not found"));

        User assigned = null;
        if (req.getAssignedToId() != null) {
            assigned = userRepository.findById(req.getAssignedToId())
                    .orElseThrow(() -> new NotFoundException("assigned user not found"));
        }

        task.setAssignedTo(assigned);
        task.setVersion(task.getVersion() + 1);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        saveVersionSnapshot(task);

        activityRepo.save(ActivityEvent.builder()
                .id(UUID.randomUUID())
                .taskId(task.getId())
                .activityType(ActivityType.ASSIGNEE_CHANGED)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details("Assignee changed to " + (assigned != null ? assigned.getId() : "<unassigned>"))
                .build());

        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updatePriority(UUID id, UpdatePriorityRequest req) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));

        if (req.getPriority() == null) throw new IllegalArgumentException("priority is required");
        User performedBy = userRepository.findById(req.getPerformedById())
                .orElseThrow(() -> new NotFoundException("user not found"));

        task.setPriority(req.getPriority());
        task.setVersion(task.getVersion() + 1);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        saveVersionSnapshot(task);

        activityRepo.save(ActivityEvent.builder()
                .id(UUID.randomUUID())
                .taskId(task.getId())
                .activityType(ActivityType.PRIORITY_CHANGED)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details("Priority changed to " + req.getPriority())
                .build());

        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateDueDate(UUID id, UpdateDueDateRequest req) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        User performedBy = userRepository.findById(req.getPerformedById())
                .orElseThrow(() -> new NotFoundException("user not found"));

        task.setDueDate(req.getDueDate());
        task.setVersion(task.getVersion() + 1);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);

        saveVersionSnapshot(task);

        activityRepo.save(ActivityEvent.builder()
                .id(UUID.randomUUID())
                .taskId(task.getId())
                .activityType(ActivityType.DUE_DATE_CHANGED)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details("Due date changed to " + req.getDueDate())
                .build());

        return toResponse(task);
    }

    @Override
    @Transactional
    public void addComment(UUID id, CreateCommentRequest req) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        User author = userRepository.findById(req.getAuthorId()).orElseThrow(() -> new NotFoundException("author not found"));

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .task(task)
                .author(author)
                .text(req.getText())
                .timestamp(LocalDateTime.now())
                .build();
        commentRepo.save(comment);

        activityRepo.save(ActivityEvent.builder()
                .id(UUID.randomUUID())
                .taskId(task.getId())
                .activityType(ActivityType.COMMENT_ADDED)
                .performedBy(author)
                .timestamp(LocalDateTime.now())
                .details("Comment added")
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskVersionResponse> getHistory(UUID id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        List<TaskVersion> versions = versionRepo.findByTaskIdOrderByVersionNumberDesc(task.getId());
        return versions.stream().map(v -> TaskVersionResponse.builder()
                .versionNumber(v.getVersionNumber())
                .title(v.getTitle())
                .description(v.getDescription())
                .status(v.getStatus())
                .priority(v.getPriority())
                .dueDate(v.getDueDate())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build()).collect(Collectors.toList());
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .version(task.getVersion())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .dueDate(task.getDueDate())
                .tags(task.getTags())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private void saveVersionSnapshot(Task task) {
        TaskVersion version = TaskVersion.builder()
                .id(UUID.randomUUID())
                .task(task)
                .versionNumber(task.getVersion())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assignedTo(task.getAssignedTo())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
        versionRepo.save(version);
    }

}

