package axon.cqrs.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAggregateEventsDto {
    private String name;
    private String payload;
    private long sequenceNumber;
    private Instant timestamp;
}
