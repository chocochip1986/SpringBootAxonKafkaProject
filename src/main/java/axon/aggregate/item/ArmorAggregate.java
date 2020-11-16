package axon.aggregate.item;

import axon.cqrs.commands.item.CreateArmorCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArmorAggregate extends ItemAggregate {
    private double defense;

    @CommandHandler
    public ArmorAggregate(CreateArmorCommand cmd) {
        super(cmd.getUuid(), cmd.getName());
        this.defense = cmd.getDefense();
    }
}
