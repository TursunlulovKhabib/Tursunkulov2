package org.tursunkulov.authorization.kafka;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tursunkulov.authorization.model.AuditEventDto;
import org.tursunkulov.authorization.outbox.OutboxEventService;
import org.tursunkulov.authorization.outbox.OutboxScheduler;

@SpringBootTest(
    classes = {
        OutboxEventService.class,
        OutboxScheduler.class,
        KafkaTopicConfig.class,
        KafkaAutoConfiguration.class
    }
)
@Testcontainers
@TestPropertySource(properties = {
    "topic.audit=test-topic",
    "outbox.scheduler.delay=1000",
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.flyway.enabled=false"
})
public class OutboxSchedulerIntegrationTest {

    @Container
    static final KafkaContainer kafka =
        new KafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void registerKafka(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OutboxEventService outboxEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishFromOutboxToKafka() throws Exception {
        AuditEventDto dto = AuditEventDto.builder()
            .eventId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .action("TEST_ACTION")
            .timestamp(Instant.now())
            .build();

        outboxEventService.publishToOutbox(dto);

        try (KafkaConsumer<String, String> consumer =
                 createConsumer(kafka.getBootstrapServers(), "test-group")) {

            consumer.subscribe(List.of("test-topic"));

            Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(500));
                    assertEquals(1, records.count(), "Сообщение должно появиться в Kafka");

                    String raw = records.iterator().next().value();
                    AuditEventDto received = objectMapper.readValue(raw, AuditEventDto.class);
                    assertEquals(dto.getEventId(), received.getEventId());
                    assertEquals(dto.getUserId(), received.getUserId());
                    assertEquals(dto.getAction(), received.getAction());
                });
        }
    }

    private KafkaConsumer<String, String> createConsumer(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }
}
