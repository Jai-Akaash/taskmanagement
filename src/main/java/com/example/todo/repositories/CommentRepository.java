package com.example.todo.repositories;

import com.example.todo.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTaskIdOrderByTimestampAsc(UUID taskId);
}