package axon.aggregate;

import axon.aggregate.members.OrderTransaction;
import axon.cqrs.commands.CreateOrderCommand;
import axon.cqrs.commands.UpdateOrderCommand;
import axon.events.OrderCreatedEvent;
import axon.events.OrderTransactionCreatedEvent;
import axon.events.OrderUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Data
@AllArgsConstructor
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private UUID uuid;

    @AggregateVersion
    private int version;
    private String orderName;
    private double price;

    @AggregateMember
    private List<OrderTransaction> orderTransactions;

    public OrderAggregate() {
        orderTransactions = new ArrayList<>();
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        orderTransactions = new ArrayList<>();
        List<OrderTransactionCreatedEvent> transactionCreatedEvents = cmd.getOrderTransactionsCmd()
                .stream()
                .map(orderTransactionCommand -> OrderTransactionCreatedEvent.builder()
                        .uuid(orderTransactionCommand.getTransactionId())
                        .amount(orderTransactionCommand.getAmount())
                        .build())
                .collect(Collectors.toList());
        apply(new OrderCreatedEvent(cmd.getUuid(), cmd.getOrderName(), cmd.getPrice(), transactionCreatedEvents));
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

        orderTransactions.addAll(
                event.getTransactionEvents().stream()
                        .map(orderTransactionCreatedEvent -> {
                            return new OrderTransaction(orderTransactionCreatedEvent.getUuid(), orderTransactionCreatedEvent.getAmount());
                        })
                        .collect(Collectors.toList())
        );
    }

    @EventSourcingHandler
    public void on(OrderUpdatedEvent event) {
        this.orderName = event.getOrderName();
        this.price = event.getPrice();
    }
}
