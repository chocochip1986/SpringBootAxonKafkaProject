package axon.aggregate.members;

import axon.cqrs.commands.UpdateOrderTransactionCommand;
import axon.events.OrderTransactionUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.EntityId;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderTransaction {
    @EntityId
    private UUID transactionId;
    private double amount;

    public OrderTransaction(double amount) {
        this.amount = amount;
    }

    @CommandHandler
    public void handle(UpdateOrderTransactionCommand cmd) {
        apply(new OrderTransactionUpdatedEvent(cmd.getAmount()));
    }

    @EventSourcingHandler
    public void on(OrderTransactionUpdatedEvent event) {
        this.amount = event.getAmount();
    }
}
