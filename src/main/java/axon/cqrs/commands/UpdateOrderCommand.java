package axon.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderCommand {
    private UUID uuid;
    private String orderName;
    private double price;
}
