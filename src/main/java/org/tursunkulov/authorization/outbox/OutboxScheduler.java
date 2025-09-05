package org.tursunkulov.authorization.outbox;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxScheduler {

  private final OutboxRepository repository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  private final Timer   processingTimer;
  private final Counter runCounter;
  private final Counter successCounter;
  private final Counter failCounter;

  public OutboxScheduler(
          OutboxRepository repository,
          KafkaTemplate<String, String> kafkaTemplate,
          MeterRegistry registry,
          @Value("${topic.audit}") String auditTopic) {

    this.repository   = repository;
    this.kafkaTemplate = kafkaTemplate;

    /* ---- Timer: длительность всей пачки -------------------------------- */
    this.processingTimer = Timer.builder("outbox_processing")
            .description("Duration of outbox batch processing")
            .publishPercentileHistogram()
            .publishPercentiles(0.5, 0.75, 0.95, 0.99)
            .serviceLevelObjectives(
                    Duration.ofMillis(50),
                    Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(500),
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(5))
            .register(registry);

    /* ---- Cчётчики ------------------------------------------------------- */
    this.runCounter = Counter.builder("outbox_runs_total")
            .description("Total number of OutboxScheduler executions")
            .register(registry);

    this.successCounter = Counter.builder("outbox_messages_published_total")
            .description("Successfully published outbox messages")
            .register(registry);

    this.failCounter = Counter.builder("outbox_messages_failed_total")
            .description("Failed outbox message publications")
            .register(registry);

    /* ---- Gauge: текущий размер бэклога --------------------------------- */
    Gauge.builder("outbox_backlog_records", repository, OutboxRepository::count)
            .description("Current number of records waiting in outbox table")
            .baseUnit("records")
            .register(registry);
  }

  @Scheduled(fixedDelayString = "${outbox.scheduler.delay:5000}")
  @Transactional
  public void publishOutbox() {
    runCounter.increment();
    processingTimer.record(this::doPublish);
  }

  private void doPublish() {
    List<OutboxRecord> records = repository.findAll();
    for (OutboxRecord record : records) {
      try {
        kafkaTemplate.send(record.getUser().getId().toString(), record.getValue())
                .get(5, TimeUnit.SECONDS);
        repository.delete(record);
        successCounter.increment();
      } catch (Exception ex) {
        failCounter.increment();
      }
    }
  }
}
