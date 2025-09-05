package org.tursunkulov.authorization.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll(@RequestHeader("userId") UUID userId) {
        log.info("User {} requested all users", userId);
        return ResponseEntity.ok(userService.allUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(
        @RequestHeader("userId") UUID userId,
        @PathVariable Integer id) {
        User user = userService.findUserById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        log.info("User {} requested user by id {}", userId, id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> create(
        @RequestHeader("userId") UUID userId,
        @Valid @RequestBody User user) {
        User created = userService.createUser(user, userId);
        log.info("User {} created new user {}", userId, created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(
        @RequestHeader("userId") UUID userId,
        @PathVariable Integer id,
        @Valid @RequestBody User user) {
        User updated = userService.updateUserById(id, user, userId);
        log.info("User {} updated user {}", userId, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @RequestHeader("userId") UUID userId,
        @PathVariable Integer id) {
        userService.deleteUserById(id, userId);
        log.info("User {} deleted user {}", userId, id);
        return ResponseEntity.noContent().build();
    }
}
