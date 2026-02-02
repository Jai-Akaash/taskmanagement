package com.example.todo.repositories;

import com.example.todo.models.TaskVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskVersionRepository extends JpaRepository<TaskVersion, UUID> {
    List<TaskVersion> findByTaskIdOrderByVersionNumberDesc(UUID taskId);
}