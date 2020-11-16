package axon.services.item;

import axon.aggregate.item.WeaponAggregate;
import axon.cqrs.commands.item.CreateWeaponCommand;
import axon.cqrs.commands.item.UpdateWeaponCommand;
import axon.dtos.item.CreateItemAggregateDto;
import axon.dtos.item.UpdateItemAggregateDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ItemCommandService {
    @Autowired
    private CommandGateway commandGateway;

    public CompletableFuture<WeaponAggregate> createWeapon(CreateItemAggregateDto dto) {
        CreateWeaponCommand cmd = new CreateWeaponCommand(dto.getDamage(), dto.getName());
        return this.commandGateway.send(cmd);
    }

    public CompletableFuture<WeaponAggregate> updateWeapon(UpdateItemAggregateDto dto) {
        UpdateWeaponCommand cmd = new UpdateWeaponCommand(dto.getUuid(), dto.getName(), dto.getDamage());
        return this.commandGateway.send(cmd);
    }
}
