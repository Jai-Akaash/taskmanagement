package com.example.todo.models;

import com.example.todo.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityEvent {

    @Id
    private UUID id;

    private UUID taskId;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String details;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}
