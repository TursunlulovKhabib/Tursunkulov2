package org.tursunkulov.authorization.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.model.AuditEventDto;
import org.tursunkulov.authorization.outbox.OutboxEventService;
import org.tursunkulov.authorization.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final OutboxEventService outboxEventService;

    @Transactional
    @Cacheable("users")
    public Optional<List<User>> allUsers() {
        return Optional.ofNullable(userRepository.allUsers());
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Optional<User> findUserById(int id) {
        return userRepository.findUserById(id);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUserById(int id, UUID actorId) {
        userRepository.deleteById(id);
        sendOutbox("DELETE_BY_ID:" + id, actorId);
    }

    @Transactional
    @CacheEvict(value = "username", key = "#username")
    public void deleteUserByUsername(String username, UUID actorId) {
        userRepository.deleteByUsername(username);
        sendOutbox("DELETE_BY_USERNAME:" + username, actorId);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Optional<User> patchPhoneNumber(int id, String phoneNumber, UUID actorId) {
        Optional<User> updated = userRepository.patchPhoneNumber(id, phoneNumber);
        updated.ifPresent(u -> sendOutbox("PATCH_PHONE_NUMBER:" + id, actorId));
        return updated;
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Optional<User> patchEmail(int id, String email, UUID actorId) {
        Optional<User> updated = userRepository.patchEmail(id, email);
        updated.ifPresent(u -> sendOutbox("PATCH_EMAIL:" + id, actorId));
        return updated;
    }

    @Transactional
    @CachePut(value = "user", key = "#id")
    public void updateUserById(int id, User user, UUID actorId) {
        userRepository.updateUserById(id, user);
        sendOutbox("UPDATE_BY_ID:" + id, actorId);
    }

    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    public void updateUserByUsername(String username, User user, UUID actorId) {
        userRepository.updateUserByUsername(username, user);
        sendOutbox("UPDATE_BY_USERNAME:" + username, actorId);
    }

    private void sendOutbox(String action, UUID actorId) {
        AuditEventDto event = AuditEventDto.builder()
                .eventId(UUID.randomUUID())
                .userId(actorId)
                .action(action)
                .timestamp(Instant.now())
                .build();
        outboxEventService.publishToOutbox(event);
        log.debug("Published to outbox: {}", event);
    }
}
