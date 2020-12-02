package axon.config.axon;

import axon.aggregate.item.WeaponAggregate;
import axon.aggregate.order.OrderAggregate;
import axon.config.axon.h2db.H2dbEventTableFactory;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.gateway.DefaultEventGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.MySqlEventTableFactory;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
                .snapshotSerializer(JacksonSerializer.defaultSerializer())
                .eventSerializer(JacksonSerializer.defaultSerializer())
                .build();
        engine.createSchema(new MySqlEventTableFactory());
        return engine;
    }

    @Bean
    public EventSourcingRepository<WeaponAggregate> itemAggregateEventSourcingRepository(@Qualifier("ItemEventStore") EventStore itemEventStore,
                                                                                         SnapshotTriggerDefinition eventCountSnapshotTriggerDefinition) {
        return EventSourcingRepository
                .builder(WeaponAggregate.class)
                .eventStore(itemEventStore)
                .snapshotTriggerDefinition(eventCountSnapshotTriggerDefinition)
                .build();

    }

    @Bean
    public AggregateFactory<WeaponAggregate> weaponAggregateAggregateFactory(EventSourcingRepository<WeaponAggregate> itemAggregateEventSourcingRepository) {
        return itemAggregateEventSourcingRepository.getAggregateFactory();
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory);
        jpaTransactionManager.afterPropertiesSet();
        return jpaTransactionManager;
    }

    @Bean
    public EventCountSnapshotTriggerDefinition eventCountSnapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 1);
    }

    @Bean
    public EventGateway itemEventGateway(EventBus itemEventStore, EventMessageDispatchInterceptor eventMessageDispatchInterceptor) {
        return DefaultEventGateway.builder().eventBus(itemEventStore).dispatchInterceptors(eventMessageDispatchInterceptor).build();
    }

    @Bean
    @ConditionalOnBean(name = {"weaponAggregateAggregateFactory", "orderAggregateAggregateFactory"})
    public Snapshotter snapshotter(EmbeddedEventStore paymentConfigurationEventStore,
                                   AggregateFactory<WeaponAggregate> itemAggregateEventSourcingRepository,
                                   AggregateFactory<OrderAggregate> orderAggregateAggregateFactory) {
        return AggregateSnapshotter.builder()
                .eventStore(paymentConfigurationEventStore)
                .aggregateFactories(itemAggregateEventSourcingRepository, orderAggregateAggregateFactory)
                .build();
    }
}
