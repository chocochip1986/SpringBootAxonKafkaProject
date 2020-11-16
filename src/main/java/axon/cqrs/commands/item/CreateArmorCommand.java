package axon.cqrs.commands.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateArmorCommand {
    @TargetAggregateIdentifier
    private UUID uuid;
    private double defense;
    private String name;

    public CreateArmorCommand(double defense, String name) {
        this.uuid = UUID.randomUUID();
        this.defense = defense;
        this.name = name;
    }
}
