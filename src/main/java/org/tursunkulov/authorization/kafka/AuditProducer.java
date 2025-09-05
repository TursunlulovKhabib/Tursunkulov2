package org.tursunkulov.authorization.kafka;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.tursunkulov.authorization.model.AuditEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditProducer {

  private final KafkaTemplate<String, AuditEventDto> kafkaTemplate;

  @Value("${topic.audit}")
  private String auditTopic;

  public void send(AuditEventDto event) {
    ProducerRecord<String, AuditEventDto> record =
        new ProducerRecord<>(auditTopic, event.getUserId().toString(), event);
    record.headers().add("userId", event.getUserId().toString().getBytes(StandardCharsets.UTF_8));

    try {
      kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);
      log.info("Sent audit event: {}", event);
    } catch (Exception ex) {
      log.error("Failed to send audit event: {}", event, ex);
      throw new IllegalStateException("Could not send audit event", ex);
    }
  }
}