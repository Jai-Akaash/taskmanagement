package com.example.todo.services;


import com.example.todo.enums.UserRole;
import com.example.todo.models.User;
import com.example.todo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /* -------- Create User -------- */
    public User createUser(String name, String email, UserRole role) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .role(role)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    /* -------- Get User -------- */
    public User getUserById(UUID id) {
        return userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /* -------- List Users -------- */
    public List<User> getAllUsers(UserRole role, Boolean activeOnly) {

        if (role != null) {
            return userRepository.findByRoleAndActiveTrue(role);
        }

        if (Boolean.TRUE.equals(activeOnly)) {
            return userRepository.findByActiveTrue();
        }

        return userRepository.findAll();
    }

    /* -------- Update User -------- */
    public User updateUser(UUID id, String name, UserRole role) {
        User user = getUserById(id);

        user.setName(name);
        user.setRole(role);

        return userRepository.save(user);
    }

    /* -------- Soft Delete -------- */
    public void deleteUser(UUID id, boolean hasActiveTasks) {

        if (hasActiveTasks) {
            throw new IllegalStateException(
                    "Cannot delete user with active assigned tasks"
            );
        }

        User user = getUserById(id);
        user.setActive(false);

        userRepository.save(user);
    }
}
