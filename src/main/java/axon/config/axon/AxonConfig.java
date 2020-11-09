package axon.config.axon;

import axon.aggregate.OrderAggregate;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class AxonConfig {
    @Bean
    public EmbeddedEventStore eventStore(EventStorageEngine storageEngine,
                                         AxonConfiguration axonConfiguration,
                                         EventMessageDispatchInterceptor eventMessageDispatchInterceptor) {
        EmbeddedEventStore embeddedEventStore =  EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .messageMonitor(axonConfiguration.messageMonitor(EventStore.class, "eventStore"))
                .build();
        embeddedEventStore.registerDispatchInterceptor(eventMessageDispatchInterceptor);
        return embeddedEventStore;
    }

    @Bean
    public EventStorageEngine storageEngine() {
        return new InMemoryEventStorageEngine();
    }

    @Bean
    public EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository(EventStore eventStore) {
        return EventSourcingRepository
                .builder(OrderAggregate.class)
                .eventStore(eventStore)
                .build();
    }

    @Bean
    public CommandGateway commandGateway(CommandMessageInterceptor commandMessageInterceptor,
                                         CommandBus commandBus) {
        return CommandGatewayFactory.builder()
                .commandBus(commandBus)
                .dispatchInterceptors(commandMessageInterceptor)
                .build()
                .createGateway(CommandGateway.class);
    }
}
