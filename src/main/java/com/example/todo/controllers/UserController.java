package com.example.todo.controllers;

import com.example.todo.dto.CreateUserRequest;
import com.example.todo.dto.UpdateUserRequest;
import com.example.todo.enums.UserRole;
import com.example.todo.models.User;
import com.example.todo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* -------- Create User -------- */
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getRole()
        );
    }

    /* -------- Get User -------- */
    @GetMapping("/{id}")
    public User getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    /* -------- List Users -------- */
    @GetMapping
    public List<User> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "true") Boolean activeOnly
    ) {
        return userService.getAllUsers(role, activeOnly);
    }

    /* -------- Update User -------- */
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(
                id,
                request.getName(),
                request.getRole()
        );
    }

    /* -------- Soft Delete -------- */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        // later: call TaskService to check active tasks
        boolean hasActiveTasks = false;
        userService.deleteUser(id, hasActiveTasks);
    }
}
