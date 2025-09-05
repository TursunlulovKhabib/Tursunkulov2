package org.tursunkulov.authorization.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${topic.audit}")
    private String auditTopic;

    @Scheduled(fixedDelayString = "${outbox.scheduler.delay:5000}")
    public void publishOutbox() {
        List<OutboxRecord> all = repository.findAll();
        for (OutboxRecord rec : all) {
            try {
                kafkaTemplate.send(auditTopic, rec.getValue())
                        .get(2, TimeUnit.SECONDS);
                repository.delete(rec);
                log.debug("Outbox record {} sent and removed", rec.getId());
            } catch (Exception ex) {
                log.error("Failed to send outbox record {}, will retry later", rec.getId(), ex);
            }
        }
    }
}
