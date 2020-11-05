package axon.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private UUID uuid;
    private String orderName;
    private double price;
    public OrderCreatedEvent(String orderName, double price) {
        this.uuid = UUID.randomUUID();
        this.orderName = orderName;
        this.price = price;
    }
}
