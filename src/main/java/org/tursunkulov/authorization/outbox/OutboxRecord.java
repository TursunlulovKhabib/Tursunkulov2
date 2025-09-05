package org.tursunkulov.authorization.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "outbox")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxRecord {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;
}
