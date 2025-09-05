package org.tursunkulov.authorization.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final OutboxEventService outboxEventService;

    @Cacheable(value = "users")
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "user", key = "#id")
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(User user, UUID actorId) {
        User saved = userRepository.save(user);
        publishOutbox("CREATE:" + saved.getId(), actorId);
        return saved;
    }

    @Transactional
    @CachePut(value = "user", key = "#id")
    public User updateUserById(Integer id, User user, UUID actorId) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        existing.setPhoneNumber(user.getPhoneNumber());
        User updated = userRepository.save(existing);
        publishOutbox("UPDATE_BY_ID:" + id, actorId);
        return updated;
    }

    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void deleteUserById(Integer id, UUID actorId) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
        publishOutbox("DELETE_BY_ID:" + id, actorId);
    }

    private void publishOutbox(String action, UUID actorId) {
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
