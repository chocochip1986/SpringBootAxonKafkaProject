package axon.event_handlers;

import axon.aggregate.item.WeaponAggregate;
import axon.events.item.WeaponAggregateCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Implicitly, If a bean contains method with @EventHandler annotations, these event handlers will all be registered automatically into one single processing group.
//The processing group is the this class's package name. So you don't need to specify the @ProcessingGroup annotation unless, perhaps, you want a different name.
//Futhermore, you don't need to register the eventHandler class via the EventProcessingConfigurer as Axon will settle it for you via spring beans.
//@ProcessingGroup("axon.event_handlers")
@Service
public class WeaponAggregateExternalEventHandler {
    @Autowired
    EventGateway itemEventGateway;

    @EventHandler
    public void on(WeaponAggregateCreatedEvent event) {
//        WeaponAggregate weaponAggregate = new WeaponAggregate();
//        weaponAggregate.on(event);
        System.out.println("[Thread-"+Thread.currentThread().getId()+"]: "+Thread.currentThread().toString()+"\n[Event]: "+event.toString());
        itemEventGateway.publish(event);
    }
}
