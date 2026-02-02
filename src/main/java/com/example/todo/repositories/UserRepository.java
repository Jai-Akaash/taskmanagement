package com.example.todo.repositories;


import com.example.todo.enums.UserRole;
import com.example.todo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByIdAndActiveTrue(UUID id);

    List<User> findByActiveTrue();

    List<User> findByRoleAndActiveTrue(UserRole role);
}
