package org.tursunkulov.authorization.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tursunkulov.authorization.model.AuditEventDto;

import java.util.UUID;

@Service
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
            throw new IllegalStateException("Cannot serialize AuditEventDto to JSON", e);
        }
        OutboxRecord rec = OutboxRecord.builder()
                .id(UUID.randomUUID())
                .value(json)
                .build();
        repository.save(rec);
    }
}
