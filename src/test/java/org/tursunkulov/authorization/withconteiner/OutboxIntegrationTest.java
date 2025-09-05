package org.tursunkulov.authorization.withconteiner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.tursunkulov.authorization.model.AuditEventDto;
import org.tursunkulov.authorization.outbox.OutboxEventService;
import org.tursunkulov.authorization.outbox.OutboxRepository;
import org.tursunkulov.authorization.outbox.OutboxScheduler;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class OutboxIntegrationTest {

    @Container
    static PostgreSQLContainer<?> pg =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"))
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Container
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Autowired
    private OutboxEventService outbox;

    @Autowired
    private OutboxRepository repository;

    @Autowired
    private OutboxScheduler scheduler;

    @Autowired
    private ObjectMapper objectMapper;

    static KafkaConsumer<String, String> consumer;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", () -> pg.getJdbcUrl());
        reg.add("spring.datasource.username", () -> pg.getUsername());
        reg.add("spring.datasource.password", () -> pg.getPassword());

        reg.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        reg.add("topic.audit", () -> "audit-topic");
    }

    private KafkaConsumer<String, String> getConsumer() {
        if (consumer == null) {
            Map<String, Object> props = KafkaTestUtils.consumerProps(
                    "testGroup", "true", kafka.getBootstrapServers());
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(java.util.List.of("audit-topic"));
        }
        return consumer;
    }

    @Test
    void whenOutboxRecord_thenSchedulerPublishesToKafkaAndDeletes() throws Exception {
        // prepare
        UUID actor = UUID.randomUUID();
        AuditEventDto evt = AuditEventDto.builder()
                .eventId(UUID.randomUUID())
                .userId(actor)
                .action("TEST")
                .timestamp(Instant.now())
                .build();

        outbox.publishToOutbox(evt);
        assertThat(repository.findAll()).hasSize(1);

        scheduler.publishOutbox();

        ConsumerRecords<String, String> recs = getConsumer().poll(java.time.Duration.ofSeconds(5));
        assertThat(recs.count()).isEqualTo(1);
        String payload = recs.iterator().next().value();
        AuditEventDto received = objectMapper.readValue(payload, AuditEventDto.class);
        assertThat(received).usingRecursiveComparison().isEqualTo(evt);

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void whenKafkaDown_thenOutboxKeepsRecord() {
        // подготовка
        UUID actor = UUID.randomUUID();
        AuditEventDto evt = AuditEventDto.builder()
                .eventId(UUID.randomUUID())
                .userId(actor)
                .action("FAIL_TEST")
                .timestamp(Instant.now())
                .build();

        outbox.publishToOutbox(evt);
        assertThat(repository.findAll()).hasSize(1);

        kafka.stop();

        scheduler.publishOutbox();

        assertThat(repository.findAll()).hasSize(1);
    }
}

