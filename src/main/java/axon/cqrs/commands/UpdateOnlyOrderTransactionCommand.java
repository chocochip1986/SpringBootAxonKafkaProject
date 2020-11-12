package axon.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.EntityId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOnlyOrderTransactionCommand {
    @TargetAggregateIdentifier
    private UUID uuid;

    @EntityId
    private UUID transactionId;
    private double amount;
}
