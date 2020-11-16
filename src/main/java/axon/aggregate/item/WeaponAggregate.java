package axon.aggregate.item;

import axon.cqrs.commands.item.CreateWeaponCommand;
import axon.cqrs.commands.item.UpdateWeaponCommand;
import axon.events.item.WeaponAggregateCreatedEvent;
import axon.events.item.WeaponAggregateUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeaponAggregate extends ItemAggregate {
    private double damage;

    @CommandHandler
    public WeaponAggregate(CreateWeaponCommand cmd) {
        apply(WeaponAggregateCreatedEvent.builder().uuid(cmd.getUuid()).name(cmd.getName()).damage(cmd.getDamage()).build());
    }

    @CommandHandler
    public void handle(UpdateWeaponCommand cmd) {
        apply(WeaponAggregateUpdatedEvent.builder().name(cmd.getName()).damage(cmd.getDamage()).build());
    }

    @EventSourcingHandler
    public void on(WeaponAggregateCreatedEvent event) {
        this.uuid = event.getUuid();
        this.name = event.getName();
        this.damage = event.getDamage();
    }

    @EventSourcingHandler
    public void on(WeaponAggregateUpdatedEvent event) {
        this.name = event.getName();
        this.damage = event.getDamage();
    }
}
