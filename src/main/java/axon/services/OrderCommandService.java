package axon.services;

import axon.aggregate.OrderAggregate;
import axon.cqrs.commands.CreateOrderCommand;
import axon.cqrs.commands.UpdateOrderCommand;
import axon.dtos.CreateOrderAggregateDto;
import axon.dtos.UpdateOrderAggregateDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderCommandService {
    @Autowired
    private CommandGateway commandGateway;

    public CompletableFuture<OrderAggregate> createOrder(CreateOrderAggregateDto dto) {
        return this.commandGateway.send(new CreateOrderCommand(dto.getOrderName(), dto.getPrice()));
    }

    public CompletableFuture<OrderAggregate> updateOrder(UpdateOrderAggregateDto dto) {
        return this.commandGateway.send(new UpdateOrderCommand(dto.getUuid(), dto.getOrderName(), dto.getPrice()));
    }
}
