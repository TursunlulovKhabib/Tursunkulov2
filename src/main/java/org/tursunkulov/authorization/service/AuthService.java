package org.tursunkulov.authorization.service;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.repository.AuthRepository;

@Service
public class AuthService {

    private AuthRepository authRepository;

    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    public void registration(User user) {
        authRepository.saveUser(user);
    }

    @Transactional
    @CachePut(value = "username", key = "#username.toString()")
    public void checkUser(String username, String password) {
        authRepository.checkUser(username, password);
    }
}
