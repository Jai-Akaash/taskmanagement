package com.example.todo.repositories;

import com.example.todo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> ,
        JpaSpecificationExecutor<Task> {
}