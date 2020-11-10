package axon.cqrs.commands;

import axon.dtos.CreateOrderTransactionAggregateMemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private UUID uuid;
    private String orderName;
    private double price;

    private List<CreateOrderTransactionCommand> orderTransactionsCmd;

    public CreateOrderCommand(String orderName, double price, List<CreateOrderTransactionCommand> transactions) {
        this.uuid = UUID.randomUUID();
        this.orderName = orderName;
        this.price = price;
        System.out.println("Creating order with UUID: "+uuid.toString());
        orderTransactionsCmd = transactions;
    }
}
