package axon.config.axon;

import axon.aggregate.item.ItemAggregate;
import axon.aggregate.item.WeaponAggregate;
import axon.aggregate.order.OrderAggregate;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
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
public class AxonConfig {
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

    @Bean("OrderStorageEngine")
    @Primary
    public EventStorageEngine orderStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

    @Bean("ItemStorageEngine")
    public EventStorageEngine itemStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

    @Bean
    public EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository(@Qualifier("OrderEventStore") EventStore orderEventStore) {
        return EventSourcingRepository
                .builder(OrderAggregate.class)
                .eventStore(orderEventStore)
                .build();
    }

    @Bean
    public EventSourcingRepository<WeaponAggregate> itemAggregateEventSourcingRepository(@Qualifier("ItemEventStore") EventStore itemEventStore) {
        return EventSourcingRepository
                .builder(WeaponAggregate.class)
                .eventStore(itemEventStore)
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
    public KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer(Configurer configurer) {
        KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer = new KafkaMessageSourceConfigurer();
        configurer.registerModule(kafkaMessageSourceConfigurer);
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
                                   KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
        SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource = SubscribableKafkaMessageSource.<String, byte[]>builder()
                .topics(Arrays.asList(kafkaProperties.getDefaultTopic()))
                .groupId("group-id")
                .consumerFactory(consumerAxonFactory)
                .fetcher(fetcher)
                .messageConverter(kafkaMessageConverter)
                .consumerCount(10)
                .build();
        kafkaMessageSourceConfigurer.registerSubscribableSource(configuration -> subscribableKafkaMessageSource);

        return subscribableKafkaMessageSource;
    }

    @Autowired
    public void useJacksonSerializer(Configurer configurer) {
        JacksonSerializer jacksonSerializer = JacksonSerializer.builder().build();
        configurer.configureEventSerializer(configuration -> jacksonSerializer)
                .configureMessageSerializer(configuration -> jacksonSerializer);
    }

    @Autowired
    public void registerPublisherToEventProcessor(EventProcessingConfigurer eventProcessingConfigurer,
                                                  KafkaEventPublisher<String,byte[]> kafkaEventPublisher,
                                                  SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource) {
        eventProcessingConfigurer
                .registerEventHandler(configuration -> kafkaEventPublisher)
                .assignHandlerTypesMatching(KafkaEventPublisher.DEFAULT_PROCESSING_GROUP,
                        clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class))
                .registerSubscribingEventProcessor(KafkaEventPublisher.DEFAULT_PROCESSING_GROUP, configuration -> subscribableKafkaMessageSource );
    }

    //PRODUCER RELATED BEANS
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
                .topic(defaultTopic)
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
