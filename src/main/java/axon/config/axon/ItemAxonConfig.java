package axon.config.axon;

import axon.aggregate.item.WeaponAggregate;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemAxonConfig {
    @Bean("ItemEventStore")
    public EmbeddedEventStore itemEventStore(@Qualifier("ItemStorageEngine") EventStorageEngine itemStorageEngine,
                                             AxonConfiguration axonConfiguration,
                                             EventMessageDispatchInterceptor eventMessageDispatchInterceptor) {
        EmbeddedEventStore embeddedEventStore =  EmbeddedEventStore.builder()
                .storageEngine(itemStorageEngine)
                .messageMonitor(axonConfiguration.messageMonitor(EventStore.class, "eventStore"))
                .build();
        embeddedEventStore.registerDispatchInterceptor(eventMessageDispatchInterceptor);
        return embeddedEventStore;
    }

    @Bean("ItemStorageEngine")
    public EventStorageEngine itemStorageEngine(Serializer serializer,
                                                PersistenceExceptionResolver persistenceExceptionResolver,
                                                Serializer eventSerializer,
                                                AxonConfiguration configuration,
                                                EntityManagerProvider entityManagerProvider,
                                                TransactionManager transactionManager) {
        return JpaEventStorageEngine.builder()
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(transactionManager)
                .eventSerializer(eventSerializer)
                .persistenceExceptionResolver(persistenceExceptionResolver)
                .upcasterChain(configuration.upcasterChain())
                .snapshotSerializer(serializer)
                .build();
    }

    @Bean
    public EventSourcingRepository<WeaponAggregate> itemAggregateEventSourcingRepository(@Qualifier("ItemEventStore") EventStore itemEventStore) {
        return EventSourcingRepository
                .builder(WeaponAggregate.class)
                .eventStore(itemEventStore)
                .build();
    }
}
