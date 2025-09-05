package org.tursunkulov.authorization.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditEventDto {
  private UUID eventId;
  private UUID userId;
  private String action;
  private Instant timestamp;
}
