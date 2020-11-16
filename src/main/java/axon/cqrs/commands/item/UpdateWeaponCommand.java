package axon.cqrs.commands.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWeaponCommand {
    @TargetAggregateIdentifier
    private UUID uuid;
    private String name;
    private double damage;
}
