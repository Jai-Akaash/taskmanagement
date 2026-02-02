package com.example.todo.repositories;

import com.example.todo.models.ActivityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, UUID> {
}