package org.tursunkulov.authorization.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.tursunkulov.authorization.entity.User;
import org.tursunkulov.authorization.kafka.AuditProducer;
import org.tursunkulov.authorization.model.AuditEventDto;
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
    private final AuditProducer auditProducer;

    @Transactional
    @Cacheable("users")
    public Optional<List<User>> allUsers() {
        // Без аудита чтения списка
        return Optional.ofNullable(userRepository.allUsers());
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Optional<User> findUserById(int id) {
        // Без аудита простого чтения
        return userRepository.findUserById(id);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUserById(int id, UUID actorId) {
        userRepository.deleteById(id);
        sendAudit("DELETE_BY_ID:" + id, actorId);
    }

    @Transactional
    @CacheEvict(value = "username", key = "#username")
    public void deleteUserByUsername(String username, UUID actorId) {
        userRepository.deleteByUsername(username);
        sendAudit("DELETE_BY_USERNAME:" + username, actorId);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Optional<User> patchPhoneNumber(int id, String phoneNumber, UUID actorId) {
        Optional<User> updated = userRepository.patchPhoneNumber(id, phoneNumber);
        updated.ifPresent(u -> sendAudit("PATCH_PHONE_NUMBER:" + id, actorId));
        return updated;
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Optional<User> patchEmail(int id, String email, UUID actorId) {
        Optional<User> updated = userRepository.patchEmail(id, email);
        updated.ifPresent(u -> sendAudit("PATCH_EMAIL:" + id, actorId));
        return updated;
    }

    @Transactional
    @CachePut(value = "user", key = "#id")
    public void updateUserById(int id, User user, UUID actorId) {
        userRepository.updateUserById(id, user);
        sendAudit("UPDATE_BY_ID:" + id, actorId);
    }

    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    public void updateUserByUsername(String username, User user, UUID actorId) {
        userRepository.updateUserByUsername(username, user);
        sendAudit("UPDATE_BY_USERNAME:" + username, actorId);
    }

    /**
     * Формирует и отправляет AuditEventDto в Kafka.
     *
     * @param action  краткое описание действия
     * @param actorId UUID пользователя, инициировавшего действие (берётся из header'а)
     */
    private void sendAudit(String action, UUID actorId) {
        AuditEventDto event = AuditEventDto.builder()
                .eventId(UUID.randomUUID())
                .userId(actorId)
                .action(action)
                .timestamp(Instant.now())
                .build();
        auditProducer.send(event);
        log.debug("Audit event sent: {}", event);
    }
}
