package org.tursunkulov.authorization.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tursunkulov.authorization.model.AuditEventDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxEventService {

  private final OutboxRepository repository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void publishToOutbox(AuditEventDto event) {
    String json;
    try {
      json = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      log.error("Cannot serialize AuditEventDto to JSON: {}", event, e);
      throw new IllegalStateException("Cannot serialize AuditEventDto to JSON", e);
    }

    OutboxRecord record = OutboxRecord.builder()
        .value(json)
        .user(null)
        .build();
    repository.save(record);
    log.debug("Persisted audit event to outbox, id={}", record.getId());
  }
}