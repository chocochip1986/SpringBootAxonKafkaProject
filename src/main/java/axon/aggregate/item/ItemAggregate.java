package axon.aggregate.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ItemAggregate {
    @AggregateIdentifier
    protected UUID uuid;

    @AggregateVersion
    protected long id;

    protected String name;

    protected ItemAggregate(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
    //1. It is not allowed to have a constructor  annotated with @CommandHandler on abstract aggregate.
    //2. If there are handlers on the abstract class, those will be present on all aggregate types children
    //3. In a polymorphic aggregate hierarchy it is not allowed to have multiple @AggregateIdentifier and @AggregateVersion
    //   annotated fields.
}
