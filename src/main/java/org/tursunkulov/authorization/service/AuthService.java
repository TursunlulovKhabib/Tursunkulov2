package org.tursunkulov.authorization.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.repository.AuthRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final AuthRepository authRepository;

  @Transactional
  @CacheEvict(value = "users", allEntries = true)
  public User registration(User user) {
    return authRepository.save(user);
  }

  @Cacheable(value = "user", key = "#username")
  public Optional<User> checkUser(String username, String password) {
    return authRepository.findByUsernameAndPassword(username, password);
  }
}
