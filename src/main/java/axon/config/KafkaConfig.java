package axon.config;

import javassist.bytecode.ByteArray;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
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
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
public class KafkaConfig {
    @Value(value = "${axon.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${axon.kafka.client-id}")
    private String clientId;

    @Value(value = "${axon.kafka.default-topic}")
    private String defaultTopic;

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
        return new KafkaMessageSourceConfigurer();
    }

    @Autowired
    public void registerKafkaMessageSourceConfigurer(Configurer configurer,
                                                     KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
        configurer.registerModule(kafkaMessageSourceConfigurer);
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
    public SubscribableKafkaMessageSource<String, ByteArray>
    subscribableKafkaMessageSource(KafkaProperties kafkaProperties,
                                   ConsumerFactory consumerFactory,
                                   Fetcher<String, ByteArray, EventMessage> fetcher,
                                   KafkaMessageConverter<String, byte[]> kafkaMessageConverter,
                                   KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
        SubscribableKafkaMessageSource<String, ByteArray> subscribableKafkaMessageSource = SubscribableKafkaMessageSource.builder()
                .topics(new ArrayList<String>(Collections.singleton("order-topic")))
                .groupId("group-id")
                .consumerFactory(consumerFactory)
                .fetcher(fetcher)
                .messageConverter(kafkaMessageConverter)
                .build();
        kafkaMessageSourceConfigurer.registerSubscribableSource(configuration -> subscribableKafkaMessageSource);

        return subscribableKafkaMessageSource;
    }

    @Autowired
    public void configureSubscribableKafkaSource(EventProcessingConfigurer eventProcessingConfigurer,
                                                 SubscribableKafkaMessageSource<String, ByteArray> subscribableKafkaMessageSource) {
        eventProcessingConfigurer.registerSubscribingEventProcessor("kafka-group", configuration -> subscribableKafkaMessageSource );
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
}
