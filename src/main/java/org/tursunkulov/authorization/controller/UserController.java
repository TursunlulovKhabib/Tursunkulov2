package org.tursunkulov.authorization.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@RateLimiter(name = "apiRateLimiter")
@CircuitBreaker(name = "apiCircuitBreaker")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<Optional<List<User>>> info(
            @RequestHeader("userId") UUID userId) {
        log.info("User {} requested all users", userId);
        return ResponseEntity.ok(userService.allUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUser(
            @RequestHeader("userId") UUID userId,
            @PathVariable int id) {
        log.info("User {} requested user by id {}", userId, id);
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("userId") UUID userId,
            @PathVariable int id) {
        userService.deleteUserById(id, userId);
        log.info("User {} deleted user by id {}", userId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/username/{username}")
    public ResponseEntity<Void> deleteByUsername(
            @RequestHeader("userId") UUID userId,
            @PathVariable String username) {
        userService.deleteUserByUsername(username, userId);
        log.info("User {} deleted user by username {}", userId, username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/patchPhoneNumber/{id}/{phoneNumber}")
    public ResponseEntity<Optional<User>> patchPhoneNumber(
            @RequestHeader("userId") UUID userId,
            @PathVariable int id,
            @PathVariable String phoneNumber) {
        log.info("User {} patching phone number for id {} → {}", userId, id, phoneNumber);
        return ResponseEntity.ok(userService.patchPhoneNumber(id, phoneNumber, userId));
    }

    @PatchMapping("/patchEmail/{id}/{email}")
    public ResponseEntity<Optional<User>> patchEmail(
            @RequestHeader("userId") UUID userId,
            @PathVariable int id,
            @PathVariable String email) {
        log.info("User {} patching email for id {} → {}", userId, id, email);
        return ResponseEntity.ok(userService.patchEmail(id, email, userId));
    }

    @PutMapping("/updateById/{id}")
    public ResponseEntity<Void> updateUserById(
            @RequestHeader("userId") UUID userId,
            @PathVariable int id,
            @RequestBody User user) {
        userService.updateUserById(id, user, userId);
        log.info("User {} updating user by id {}: {}", userId, id, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateByUsername/{username}")
    public ResponseEntity<Void> updateUserByUsername(
            @RequestHeader("userId") UUID userId,
            @PathVariable String username,
            @RequestBody User user) {
        userService.updateUserByUsername(username, user, userId);
        log.info("User {} updating user by username {}: {}", userId, username, user);
        return ResponseEntity.noContent().build();
    }
}
