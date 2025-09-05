package org.tursunkulov.authorization.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.repository.AuthRepository;
import org.tursunkulov.authorization.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class AuthControllerTestForDataBase {

  @Autowired private AuthRepository authRepository;

  @Autowired private UserRepository userRepository;

  User firstUser = new User("Max", "1234", "maxim@gmail.com", "+79226762223");
  User secondUser = new User("Oleg", "5678", "goodjob@gmail.com", "+79665292243");

  @Test
  void shouldFindUserById() {
    authRepository.save(firstUser);
    authRepository.save(secondUser);
    assertTrue(userRepository.findUserById(firstUser.getId()).isPresent());
    assertTrue(userRepository.findUserById(secondUser.getId()).isPresent());
  }

  @Test
  void shouldNotFindUserByEmail() {
    authRepository.save(firstUser);
    authRepository.save(secondUser);
    assertTrue(userRepository.findUserById(firstUser.getId()).isEmpty());
    assertTrue(userRepository.findUserById(secondUser.getId()).isEmpty());
  }

  @Test
  void shouldAuthenticateUser() {
    authRepository.save(firstUser);
    authRepository.save(secondUser);
    assertTrue(userRepository.findUserById(firstUser.getId()).isPresent());
    assertTrue(userRepository.findUserById(secondUser.getId()).isPresent());
    assertEquals(
        firstUser.getUsername(),
        userRepository.findUserById(firstUser.getId()).get().getUsername());
    assertEquals(
        secondUser.getUsername(),
        userRepository.findUserById(secondUser.getId()).get().getUsername());
  }

  @Test
  void shouldNotAuthenticateUser() {
    assertEquals(firstUser.getUsername(), userRepository.findUserById(firstUser.getId()).isEmpty());
    assertEquals(
        secondUser.getUsername(), userRepository.findUserById(secondUser.getId()).isEmpty());
  }
}
