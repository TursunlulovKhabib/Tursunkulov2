package org.tursunkulov.authorization.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.service.AuthService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/login")
@RateLimiter(name = "apiRateLimiter")
@CircuitBreaker(name = "apiCircuitBreaker")
public class AuthController implements AuthControllerApi {

    private final AuthService authService;

    @Override
    @PostMapping("/registration")
    public ResponseEntity<Void> registration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String phoneNumber) {
        User user = new User(username, password, email, phoneNumber);
        log.debug("Регистрация пользователя");
        authService.registration(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/authorization")
    public ResponseEntity<Void> authentication(
            @RequestParam String username, @RequestParam String password) {
        log.debug("Аутентификация пользователя");
        authService.checkUser(username, password);
        return ResponseEntity.noContent().build();
    }
}
