package axon.services;

import axon.aggregate.order.OrderAggregate;
import axon.cqrs.commands.CreateOnlyOrderTransactionCommand;
import axon.cqrs.commands.CreateOrderCommand;
import axon.cqrs.commands.CreateOrderTransactionCommand;
import axon.cqrs.commands.UpdateOnlyOrderTransactionCommand;
import axon.cqrs.commands.UpdateOrderCommand;
import axon.cqrs.commands.UpdateOrderTransactionCommand;
import axon.dtos.CreateOnlyOrderTransactionAggregateMemberDto;
import axon.dtos.CreateOrderAggregateDto;
import axon.dtos.CreateOrderTransactionAggregateMemberDto;
import axon.dtos.UpdateOnlyOrderTransactionAggregateMemberDto;
import axon.dtos.UpdateOrderAggregateDto;
import axon.dtos.UpdateOrderTransactionAggregateMemberDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderCommandService {
    @Autowired
    private CommandGateway commandGateway;

    public CompletableFuture<OrderAggregate> createOrder(CreateOrderAggregateDto dto) {
        List<CreateOrderTransactionCommand> cmds = new ArrayList<>();
        for(CreateOrderTransactionAggregateMemberDto createOrderTransaction : dto.getOrderTransactions()) {
            cmds.add(
                    new CreateOrderTransactionCommand(createOrderTransaction.getAmount()));
        }
        return this.commandGateway.send(
                new CreateOrderCommand(
                        dto.getOrderName(),
                        dto.getPrice(),
                        cmds));
    }

    public CompletableFuture<OrderAggregate> updateOrder(UpdateOrderAggregateDto dto) {
        List<UpdateOrderTransactionCommand> cmds = new ArrayList<>();
        for(UpdateOrderTransactionAggregateMemberDto updateOrderTransaction : dto.getOrderTransactions()) {
            cmds.add(
                    new UpdateOrderTransactionCommand(updateOrderTransaction.getUuid(), updateOrderTransaction.getAmount()));
        }
        return this.commandGateway.send(new UpdateOrderCommand(dto.getUuid(), dto.getOrderName(), dto.getPrice(), cmds));
    }

    public CompletableFuture<OrderAggregate> updateOrderTransaction(UpdateOnlyOrderTransactionAggregateMemberDto dto) {
        return this.commandGateway.send(new UpdateOnlyOrderTransactionCommand(dto.getOrderUuid(), dto.getOrderTransactionUuid(), dto.getAmount()));
    }

    public CompletableFuture<OrderAggregate> createOrderTransaction(CreateOnlyOrderTransactionAggregateMemberDto dto) {
        return this.commandGateway.send(new CreateOnlyOrderTransactionCommand(dto.getOrderUuid(), dto.getAmount()));
    }
}
