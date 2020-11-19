package axon.config.axon;

import axon.aggregate.item.WeaponAggregate;
import axon.config.axon.h2db.H2dbEventTableFactory;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;

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
    public EventStorageEngine itemStorageEngine(ConnectionProvider connectionProvider,
                                                TransactionManager transactionManager) {
        EventSchema eventSchema = new EventSchema.Builder().eventTable("DOMAIN_EVENT_ENTRY").snapshotTable("SNAPSHOT_EVENT_ENTRY").build();
        JdbcEventStorageEngine engine = JdbcEventStorageEngine.builder()
                .connectionProvider(connectionProvider)
                .transactionManager(transactionManager)
                .schema(eventSchema)
                .build();
        engine.createSchema(new H2dbEventTableFactory());
        return engine;
    }

    @Bean
    public EventSourcingRepository<WeaponAggregate> itemAggregateEventSourcingRepository(@Qualifier("ItemEventStore") EventStore itemEventStore) {
        return EventSourcingRepository
                .builder(WeaponAggregate.class)
                .eventStore(itemEventStore)
                .build();
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory);
        jpaTransactionManager.afterPropertiesSet();
        return jpaTransactionManager;
    }
}
