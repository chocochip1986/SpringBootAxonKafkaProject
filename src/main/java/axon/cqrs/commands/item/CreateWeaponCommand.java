package axon.cqrs.commands.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWeaponCommand {
    @TargetAggregateIdentifier
    private UUID uuid;
    private double damage;
    private String name;

    public CreateWeaponCommand(double damage, String name) {
        this.uuid = UUID.randomUUID();
        this.damage = damage;
        this.name = name;
    }
}
