package org.tursunkulov.authorization.kafka;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.tursunkulov.authorization.model.AuditEventDto;

@SpringBootTest(
        classes = {
                AuditProducer.class,
                KafkaTopicConfig.class,
                KafkaAutoConfiguration.class
        }
)
@TestPropertySource(properties = {
        "topic.audit=test-topic",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})

@Testcontainers
public class AuditProducerTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.4.0");

    @Autowired
    private AuditProducer producer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSendEventSuccessfully() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        AuditEventDto dto = AuditEventDto.builder()
                .eventId(UUID.randomUUID())
                .userId(userId)
                .action("TEST_ACTION")
                .timestamp(Instant.now())
                .build();

        // when / then
        assertDoesNotThrow(() -> producer.send(dto));

        // verify in Kafka
        KafkaConsumer<String, String> consumer = createConsumer(kafka.getBootstrapServers(), "test-group");
        consumer.subscribe(List.of("test-topic"));
        ConsumerRecords<String, String> records = consumer.poll(java.time.Duration.ofSeconds(5));
        assertEquals(1, records.count());

        String raw = records.iterator().next().value();
        AuditEventDto received = objectMapper.readValue(raw, AuditEventDto.class);
        assertEquals(dto.getEventId(), received.getEventId());
        assertEquals(dto.getUserId(), received.getUserId());
        assertEquals(dto.getAction(), received.getAction());
    }

    @Test
    void shouldFailOnNullDto() {
        // DTO с null-полями должен приводить к исключению
        AuditEventDto bad = AuditEventDto.builder().build();
        assertThrows(IllegalStateException.class, () -> producer.send(bad));
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
