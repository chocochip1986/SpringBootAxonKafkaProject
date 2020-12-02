package axon.config.axon;

import axon.aggregate.order.OrderAggregate;
import axon.event_handlers.WeaponAggregateExternalEventHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.MySqlEventTableFactory;
import org.axonframework.eventsourcing.eventstore.jdbc.statements.JdbcEventStorageEngineStatements;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class OrderAxonConfig {
    @Value(value = "${axon.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${axon.kafka.client-id}")
    private String clientId;

    @Value(value = "${axon.kafka.default-topic}")
    private String defaultTopic;

    @Bean("OrderEventStore")
    @Primary
    public EmbeddedEventStore orderEventStore(@Qualifier("OrderStorageEngine") EventStorageEngine orderStorageEngine,
                                         AxonConfiguration axonConfiguration,
                                         EventMessageDispatchInterceptor eventMessageDispatchInterceptor) {
        EmbeddedEventStore embeddedEventStore =  EmbeddedEventStore.builder()
                .storageEngine(orderStorageEngine)
                .messageMonitor(axonConfiguration.messageMonitor(EventStore.class, "eventStore"))
                .build();
        embeddedEventStore.registerDispatchInterceptor(eventMessageDispatchInterceptor);
        return embeddedEventStore;
    }

//    @Bean("OrderStorageEngine")
//    @Primary
//    public EventStorageEngine orderStorageEngine() {
//        return new InMemoryEventStorageEngine();
//    }

    @Bean("OrderStorageEngine")
    @Primary
    public EventStorageEngine orderStorageEngine(ConnectionProvider connectionProvider,
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
    public EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository(@Qualifier("OrderEventStore") EventStore orderEventStore) {
        return EventSourcingRepository
                .builder(OrderAggregate.class)
                .eventStore(orderEventStore)
                .build();
    }

    @Bean
    public AggregateFactory<OrderAggregate> orderAggregateAggregateFactory(EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository) {
        return orderAggregateEventSourcingRepository.getAggregateFactory();
    }
}
