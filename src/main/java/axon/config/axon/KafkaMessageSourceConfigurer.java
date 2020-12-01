package axon.config.axon;

import org.axonframework.config.Component;
import org.axonframework.config.Configuration;
import org.axonframework.config.ModuleConfiguration;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.lifecycle.Phase;
import org.axonframework.lifecycle.ShutdownHandler;
import org.axonframework.lifecycle.StartHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KafkaMessageSourceConfigurer implements ModuleConfiguration {
    private Configuration configuration;
    private final List<Component<SubscribableKafkaMessageSource<?, ?>>> subscribableKafkaMessageSources = new ArrayList<>();

    @Override
    public void initialize(Configuration config) {
        this.configuration = config;
    }

    @Override
    public int phase() {
        return Integer.MAX_VALUE;
    }

    public void registerSubscribableSource(
            Function<Configuration, SubscribableKafkaMessageSource<?, ?>> subscribableKafkaMessageSource) {
        subscribableKafkaMessageSources.add(new Component<>(
                () -> configuration, "subscribableKafkaMessageSource", subscribableKafkaMessageSource
        ));
    }

    @Override
    public void start() {
        subscribableKafkaMessageSources.stream().map(Component::get).forEach(SubscribableKafkaMessageSource::start);
    }

    @Override
    public void shutdown() {
        subscribableKafkaMessageSources.stream().map(Component::get).forEach(SubscribableKafkaMessageSource::close);
    }
}
