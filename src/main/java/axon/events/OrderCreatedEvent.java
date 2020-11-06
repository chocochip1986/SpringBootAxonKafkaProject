package axon.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderCreatedEvent {
    private UUID uuid;
    private String orderName;
    private double price;
    public OrderCreatedEvent(UUID uuid, String orderName, double price) {
        this.uuid = uuid;
        this.orderName = orderName;
        this.price = price;
    }
}
