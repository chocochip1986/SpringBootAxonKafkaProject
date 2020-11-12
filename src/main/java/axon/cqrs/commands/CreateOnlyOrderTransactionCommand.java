package axon.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.EntityId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOnlyOrderTransactionCommand {
    @TargetAggregateIdentifier
    private UUID uuid;

    private UUID transactionId;
    private double amount;

    public CreateOnlyOrderTransactionCommand(UUID orderUuid, double amount) {
        this.uuid = orderUuid;
        this.transactionId = UUID.randomUUID();
        this.amount = amount;
    }
}
