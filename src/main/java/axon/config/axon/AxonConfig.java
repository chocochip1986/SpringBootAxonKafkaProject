package axon.config.axon;

import axon.aggregate.OrderAggregate;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AxonConfig {
    @Bean
    public EventStorageEngine inMemoryEventStore() {
        return new InMemoryEventStorageEngine();
    }

    @Bean
    public EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository(EventStore inMemoryEventStore) {
        return EventSourcingRepository.builder(OrderAggregate.class).eventStore(inMemoryEventStore).build();
    }
}
