package axon.aggregate.order.members.members;

import axon.cqrs.commands.UpdateOnlyOrderTransactionCommand;
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

    @CommandHandler
    public void handle(UpdateOnlyOrderTransactionCommand cmd) {
        apply(new OrderTransactionUpdatedEvent(cmd.getTransactionId(), cmd.getAmount()));
    }

    //The event sourcing handler in the entity performs a validation check whether the received event actually belongs to the entity.
    //This is necessary as events applied by one entity instance will also be handled by any other entity instance of the same type.
    @EventSourcingHandler
    public void on(OrderTransactionUpdatedEvent event) {
        if(this.transactionId.equals(event.getUuid())) {
            this.amount = event.getAmount();
        }
    }
}
