package org.tursunkulov.authorization.repositories;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.tursunkulov.authorization.models.User;
import org.tursunkulov.authorization.service.UserService;

import java.util.List;

@Getter
@Repository
public class UserRepository {

    private static List<User> users = List.of(new User(0, "1", "1234", "1@g.com", "8800555555"));

    public static String saveUser(User user) {
        users.add(user);
        return UserService.newUser();
    }

    public static String checkUser(User user) {
        if (users.isEmpty()) {
            UserService.registration();
        } else {
            if (user.getUsername().equals(users.get(0).getUsername()) &&
            user.getPassword().equals(users.get(0).getPassword())) {
                 return UserService.authorisation();
            } else {
                 return UserService.incorrectData();
            }
        }
        return "Error";
    }

    public static List<User> getUsers() {
        return users;
    }
}
