package axon.config;

import javassist.bytecode.ByteArray;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
public class KafkaConfig {
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
    public SubscribableKafkaMessageSource<String, ByteArray>
    subscribableKafkaMessageSource(KafkaProperties kafkaProperties,
                                   ConsumerFactory consumerFactory,
                                   Fetcher<String, ByteArray, EventMessage> fetcher,
                                   KafkaMessageConverter<String, ByteArray> kafkaMessageConverter,
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
    public ProducerFactory<String, ByteArray> producerAxonKafkaFactory(KafkaProperties kafkaProperties) {
        return new DefaultProducerFactory.Builder<String, ByteArray>()
                .configuration(kafkaProperties.buildProducerProperties())
                .producerCacheSize(10000)
                .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
                .build();
    }
}
