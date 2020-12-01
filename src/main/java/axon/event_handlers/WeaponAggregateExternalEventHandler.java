package axon.event_handlers;

import axon.aggregate.item.WeaponAggregate;
import axon.events.item.WeaponAggregateCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@ProcessingGroup("axon.event_handlers")
@Service
public class WeaponAggregateExternalEventHandler {

    @EventHandler
    public void on(WeaponAggregateCreatedEvent event) {
//        WeaponAggregate weaponAggregate = new WeaponAggregate();
//        weaponAggregate.on(event);
        System.out.println(event.toString());
    }
}
