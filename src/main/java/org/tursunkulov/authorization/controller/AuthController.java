package org.tursunkulov.authorization.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<User> register(@Valid @RequestBody User user) {
    User created = authService.registration(user);
    log.debug("Регистрация пользователя: {}", user.getUsername());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PostMapping("/login")
  public ResponseEntity<User> login(
      @RequestBody User credentials) {
    String username = credentials.getUsername();
    String password = credentials.getPassword();
    Optional<User> userOpt = authService.checkUser(username, password);
    User user = userOpt.orElseThrow(() ->
        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
    );
    log.debug("Аутентификация пользователя: {}", username);
    return ResponseEntity.ok(user);
  }
}
