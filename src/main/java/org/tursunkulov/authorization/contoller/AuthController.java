package org.tursunkulov.authorization.contoller;

import org.springframework.web.bind.annotation.*;
import org.tursunkulov.authorization.models.User;
import org.tursunkulov.authorization.repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/user")
public class AuthController {

    @PostMapping("/registartion")
    public String registration(@RequestParam int id, @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String phoneNumber) {
        User user = new User(id, username, password, email, phoneNumber);
        return UserRepository.saveUser(user);
    }

    @PostMapping("/authorization")
    public String authorization(@RequestParam int id, @RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email,
                                @RequestParam String phoneNumber) {
        User user = new User(id, username, password, email, phoneNumber);
        return UserRepository.checkUser(user);
    }

    @GetMapping("/info")
    public List<User> info(@RequestParam String username) {
        return UserRepository.getUsers();
    }
}
