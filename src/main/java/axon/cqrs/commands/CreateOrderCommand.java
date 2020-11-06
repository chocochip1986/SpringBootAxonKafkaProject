package axon.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private UUID uuid;
    private String orderName;
    private double price;

    public CreateOrderCommand(String orderName, double price) {
        this.uuid = UUID.randomUUID();
        this.orderName = orderName;
        this.price = price;
    }
}
