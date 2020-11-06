package axon.aggregate;

import axon.cqrs.commands.CreateOrderCommand;
import axon.cqrs.commands.UpdateOrderCommand;
import axon.events.OrderCreatedEvent;
import axon.events.OrderUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private UUID uuid;
    private String orderName;
    private double price;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        apply(new OrderCreatedEvent(cmd.getUuid(), cmd.getOrderName(), cmd.getPrice()));
    }

    @CommandHandler
    public void updateOrderAggregate(UpdateOrderCommand cmd) {
        apply(new OrderUpdatedEvent(cmd.getUuid(), cmd.getOrderName(), cmd.getPrice()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.uuid = event.getUuid();
        this.orderName = event.getOrderName();
        this.price = event.getPrice();
    }

    @EventSourcingHandler
    public void on(OrderUpdatedEvent event) {
        this.orderName = event.getOrderName();
        this.price = event.getPrice();
    }
}
