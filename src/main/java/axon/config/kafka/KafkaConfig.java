package axon.config.kafka;

import javassist.bytecode.ByteArray;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
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
import org.springframework.kafka.config.TopicBuilder;

import java.util.ArrayList;
import java.util.Arrays;
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
    public NewTopic topic() {
        return TopicBuilder.name(defaultTopic).build();
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
                                   ConsumerFactory<String, byte[]> consumerFactory,
                                   Fetcher<String, byte[], EventMessage<?>> fetcher,
                                   KafkaMessageConverter<String, byte[]> kafkaMessageConverter,
                                   KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
        SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource = SubscribableKafkaMessageSource.<String, byte[]>builder()
                .topics(Arrays.asList(kafkaProperties.getDefaultTopic()))
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
                                                 SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource) {
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
