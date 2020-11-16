package axon.aggregate.item;

import axon.cqrs.commands.item.CreateWeaponCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeaponAggregate extends ItemAggregate {
    private double damage;

    @CommandHandler
    public WeaponAggregate(CreateWeaponCommand cmd) {
        super(cmd.getUuid(), cmd.getName());
        this.damage = cmd.getDamage();
    }
}
