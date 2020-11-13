package axon.aggregate.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateVersion;

import java.util.UUID;

@Data
@AllArgsConstructor
public abstract class ItemAggregate {
    @AggregateIdentifier
    private UUID uuid;

    @AggregateVersion
    private long id;
}
