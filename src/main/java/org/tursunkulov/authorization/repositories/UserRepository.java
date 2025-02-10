package org.tursunkulov.authorization.repositories;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.tursunkulov.authorization.models.User;
import org.tursunkulov.authorization.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Getter
@Repository
public class UserRepository {

    private static List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(new User(1, "Пётр", "1234", "1212@g.com", "88005555555"));
        users.add(new User(2, "Andrew", "1234", "1sds12@g.com", "88885555555"));
    }

    public static String saveUser(User user) {
        users.add(user);
        return UserService.newUser();
    }

    public static String checkUser(User user) {
        if (users.isEmpty()) {
            UserRepository.saveUser(user);
        } else {
            if (users.contains(user)) {
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
