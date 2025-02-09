package org.tursunkulov.authorization.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.tursunkulov.authorization.models.User;
import org.tursunkulov.authorization.repositories.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

    public static void registration() {
        User newUser = new User(0, "", "", "", "");
        UserRepository.saveUser(newUser);
    }

    public static String newUser() {
        return "Новый пользователь добавлен";
    }

    public static String authorisation() {
        return "Добро пожаловать!";
    }

    public static String incorrectData() {
        return "Проверьте корректность данных";
    }

}