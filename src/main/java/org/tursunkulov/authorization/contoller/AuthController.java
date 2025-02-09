package org.tursunkulov.authorization.contoller;

import org.springframework.web.bind.annotation.*;
import org.tursunkulov.authorization.models.User;
import org.tursunkulov.authorization.repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/user")
public class AuthController {

    private UserRepository userRepository;

    @PostMapping("/registartion")
    public String registration(@RequestBody User user) {
        return UserRepository.saveUser(user);
    }

    @PostMapping("/authorization")
    public String authorization(@RequestBody User user) {
        return UserRepository.checkUser(user);
    }

    @GetMapping("/info")
    public List<User> info(@RequestParam String username) {
        return UserRepository.getUsers();
    }
}
