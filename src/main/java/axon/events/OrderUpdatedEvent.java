package axon.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdatedEvent {
    private UUID uuid;
    private String orderName;
    private double price;
}