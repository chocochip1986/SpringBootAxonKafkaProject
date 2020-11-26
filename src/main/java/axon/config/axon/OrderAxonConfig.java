package axon.config.axon;

import axon.aggregate.order.OrderAggregate;
import axon.config.axon.h2db.H2dbEventTableFactory;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.MySqlEventTableFactory;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
import org.axonframework.extensions.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.AsyncFetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;
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

    @Bean
    public CommandGateway commandGateway(CommandMessageInterceptor commandMessageInterceptor,
                                         CommandBus commandBus) {
        return CommandGatewayFactory.builder()
                .commandBus(commandBus)
                .dispatchInterceptors(commandMessageInterceptor)
                .build()
                .createGateway(CommandGateway.class);
    }

    @Bean
    @Primary
    public KafkaProperties kafkaProperties() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers(
                new ArrayList<>(){{
                    add(bootstrapAddress);
                }});
        kafkaProperties.setClientId(clientId);
        kafkaProperties.setDefaultTopic(defaultTopic);
        return kafkaProperties;
    }

    //CONSUMER RELATED BEANS
    @Bean
    public KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer() {
        KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer = new KafkaMessageSourceConfigurer();
        return kafkaMessageSourceConfigurer;
    }

    @Bean
    public KafkaMessageConverter<String, byte[]> kafkaMessageConverter() {
        return DefaultKafkaMessageConverter.builder().serializer(JacksonSerializer.builder().build()).build();
    }

    @Bean
    public ConsumerFactory<String, byte[]> consumerAxonFactory(KafkaProperties kafkaProperties) {
        return new DefaultConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public SubscribableKafkaMessageSource<String, byte[]>
    subscribableKafkaMessageSource(KafkaProperties kafkaProperties,
                                   ConsumerFactory<String, byte[]> consumerAxonFactory,
                                   Fetcher<String, byte[], EventMessage<?>> fetcher,
                                   KafkaMessageConverter<String, byte[]> kafkaMessageConverter,
                                   KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer,
                                   Configurer configurer) {
        SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource = SubscribableKafkaMessageSource.<String, byte[]>builder()
                .topics(Arrays.asList(kafkaProperties.getDefaultTopic()))
                .groupId("group-id")
                .consumerFactory(consumerAxonFactory)
                .fetcher(fetcher)
                .messageConverter(kafkaMessageConverter)
                .consumerCount(1)
                .autoStart()
                .build();
        subscribableKafkaMessageSource.subscribe();
        kafkaMessageSourceConfigurer.registerSubscribableSource(configuration -> subscribableKafkaMessageSource);
        configurer.registerModule(kafkaMessageSourceConfigurer);
        return subscribableKafkaMessageSource;
    }

    @Autowired
    public void configureSubscribableKafkaSource(EventProcessingConfigurer eventProcessingConfigurer,
                                                 SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource) {
        eventProcessingConfigurer.registerSubscribingEventProcessor("TEST SUBSCRIBE MSG SOURCE", configuration -> subscribableKafkaMessageSource);
    }

    @Autowired
    public void useJacksonSerializer(Configurer configurer) {
        JacksonSerializer jacksonSerializer = JacksonSerializer.builder().build();
        configurer.configureEventSerializer(configuration -> jacksonSerializer)
                .configureMessageSerializer(configuration -> jacksonSerializer);
    }

    //PRODUCER RELATED BEANS
    @Autowired
    public void registerPublisherToEventProcessor(EventProcessingConfigurer eventProcessingConfigurer,
                                                  KafkaEventPublisher<String,byte[]> kafkaEventPublisher) {
        eventProcessingConfigurer
                .registerEventHandler(configuration -> kafkaEventPublisher)
                .assignHandlerTypesMatching(KafkaEventPublisher.DEFAULT_PROCESSING_GROUP,
                        clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class)).registerSubscribingEventProcessor(KafkaEventPublisher.DEFAULT_PROCESSING_GROUP);
    }

    @Bean
    public ProducerFactory<String, byte[]> producerAxonKafkaFactory(KafkaProperties kafkaProperties) {
        return new DefaultProducerFactory.Builder<String, byte[]>()
                .configuration(kafkaProperties.buildProducerProperties())
                .producerCacheSize(10000)
                .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
                .build();
    }

    //AXON's KAFKA PUBLISHER
    //This is responsible for pushing events to a Kafka Topic
    //To achieve this it will utilize a Kafka Producer, retrieved through Axon's ProducerFactory.
    // The KafkaPublisher in turn receives the events to publish from a KafkaEventPublisher
    //It seems like it's 1 KafkaPublisher to 1 Topic
    @Bean
    public KafkaPublisher<String, byte[]> kafkaPublisher(ProducerFactory<String, byte[]> producerFactory,
                                                         KafkaMessageConverter<String, byte[]> kafkaMessageConverter) {
        return KafkaPublisher.<String, byte[]>builder()
                .topic("local.event.2")
                .producerFactory(producerFactory)
                .messageConverter(kafkaMessageConverter)
                .build();

    }

    //KAFKA EVENT PUBLISHER
    @Bean
    public KafkaEventPublisher<String, byte[]> kafkaEventPublisher(KafkaPublisher<String, byte[]> kafkaPublisher) {
        return KafkaEventPublisher.<String, byte[]>builder()
                .kafkaPublisher(kafkaPublisher)
                .build();
    }
}
