package org.tursunkulov.authorization.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void getAllUsersTest() throws Exception {
        List<User> users =
                List.of(
                        new User("Andrew", "1234", "wssw@gmail.com", "87776632233"),
                        new User("Andrey", "123wr", "swws@gmail.com", "87776632255"));
        when(userService.allUsers()).thenReturn(Optional.of(users));

        mvc.perform(get("/user/info/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].username").value("Andrew"))
                .andExpect(jsonPath("$[0].password").value("1234"))
                .andExpect(jsonPath("$[0].email").value("wssw@gmail.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("87776632233"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].username").value("Andrey"))
                .andExpect(jsonPath("$[1].password").value("123wr"))
                .andExpect(jsonPath("$[1].email").value("swws@gmail.com"))
                .andExpect(jsonPath("$[1].phoneNumber").value("87776632255"));
    }

    @Test
    @DisplayName("Тест на успешное получение имени пользователя по ID")
    public void getUserByIdTest1() throws Exception {
        User user = new User("Andrew", "1234", "wssw@gmail.com", "87776632233");
        when(userService.findUserById(3)).thenReturn(userService.findUserById(3));
        mvc.perform(get("/api/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Andrew"));
    }

    @Test
    @DisplayName("Тест на неудачное получение имени пользователя по ID : Bad Request")
    public void getUserByIdTest2() throws Exception {
        mvc.perform(get("/api/users/10000000")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест на неудачное получение имени пользователя по ID : User Not Found")
    public void getUserByIdTest3() throws Exception {
        when(userService.findUserById(111)).thenThrow(new NoSuchElementException(String.valueOf(111)));
        mvc.perform(get("/api/users/111")).andExpect(status().isNotFound());
    }
}
