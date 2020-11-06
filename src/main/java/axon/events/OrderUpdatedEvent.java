package axon.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdatedEvent {
    @TargetAggregateIdentifier
    private UUID uuid;
    private String orderName;
    private double price;
}
