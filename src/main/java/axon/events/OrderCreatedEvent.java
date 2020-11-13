package axon.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderCreatedEvent {
    private UUID uuid;
    private String orderName;
    private double price;

    private List<OrderTransactionCreatedEvent> transactionEvents;

    public OrderCreatedEvent(UUID uuid, String orderName, double price, List<OrderTransactionCreatedEvent> events) {
        this.uuid = uuid;
        this.orderName = orderName;
        this.price = price;
        transactionEvents = events;
    }
}
