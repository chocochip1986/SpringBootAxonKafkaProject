package axon.config.axon.jpa;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventUtils;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;

public class CustomJpaEventStorageEngine extends JpaEventStorageEngine {
    protected CustomJpaEventStorageEngine(Builder builder) {
        super(builder);
    }

    @Override
    protected Object createEventEntity(EventMessage<?> eventMessage, Serializer serializer) {
        return new CustomDomainEventEntry(EventUtils.asDomainEventMessage(eventMessage), serializer);
    }
}
