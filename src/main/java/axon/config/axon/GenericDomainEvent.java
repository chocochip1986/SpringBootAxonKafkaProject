package axon.config.axon;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GenericDomainEvent {
    private UUID uuid;
    private String type;
    private Object event;
    private Class<?> eventType;
}
