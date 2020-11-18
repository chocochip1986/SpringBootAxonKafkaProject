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
import org.axonframework.serialization.json.JacksonSerializer;
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
    public EventStorageEngine itemStorageEngine(PersistenceExceptionResolver persistenceExceptionResolver,
                                                AxonConfiguration configuration,
                                                EntityManagerProvider entityManagerProvider,
                                                TransactionManager transactionManager) {
        JacksonSerializer jacksonSerializer = JacksonSerializer.builder().build();
        return JpaEventStorageEngine.builder()
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(transactionManager)
                .eventSerializer(jacksonSerializer)
                .persistenceExceptionResolver(persistenceExceptionResolver)
                .upcasterChain(configuration.upcasterChain())
                .snapshotSerializer(jacksonSerializer)
                .build();
    }

    @Bean
    public EventSourcingRepository<WeaponAggregate> itemAggregateEventSourcingRepository(@Qualifier("ItemEventStore") EventStore itemEventStore) {
        return EventSourcingRepository
                .builder(WeaponAggregate.class)
                .eventStore(itemEventStore)
                .build();
    }

//    @PersistenceUnit(name = "eventStore")
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
//                                                                       EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(dataSource)
//                .packages(DomainEventEntry.class, SnapshotEventEntry.class)
//                .packages("org.axonframework.eventhandling.tokenstore.jpa", "org.axonframework.eventsourcing.eventstore.jpa")
//                .persistenceUnit("eventStore")
//                .build();
//    }
}
