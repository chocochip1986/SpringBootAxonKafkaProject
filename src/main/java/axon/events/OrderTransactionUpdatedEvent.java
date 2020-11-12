package axon.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTransactionUpdatedEvent {
//    private UUID uuid;
    private double amount;
}
