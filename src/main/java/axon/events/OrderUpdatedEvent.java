package axon.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdatedEvent {
    private UUID uuid;
    private String orderName;
    private double price;

    private List<OrderTransactionUpdatedEvent> transactionEvents;
}
