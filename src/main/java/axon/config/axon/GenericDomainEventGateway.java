package axon.config.axon;

import org.axonframework.common.BuilderUtils;
import org.axonframework.common.Registration;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventhandling.gateway.AbstractEventGateway;
import org.axonframework.messaging.GenericMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GenericDomainEventGateway {
    private final EventBus eventBus;
    private final List<MessageDispatchInterceptor<? super EventMessage<?>>> dispatchInterceptors;

    protected GenericDomainEventGateway(Builder builder) {
        this.eventBus = builder.eventBus;
        this.dispatchInterceptors = builder.dispatchInterceptors;
    }

    public static GenericDomainEventGateway.Builder builder() {
        return new GenericDomainEventGateway.Builder();
    }

    //Need aggregate identifier, type
    public void publish(List<GenericDomainEvent> events) {
        events.forEach(this::publish);
    }

    protected void publish(GenericDomainEvent event) {
        this.eventBus.publish(
                new EventMessage[]{
                        this.processInterceptors(
                                new GenericDomainEventMessage(
                                        event.getType(),
                                        event.getUuid().toString(),
                                        0l,
                                        new GenericMessage(
                                                UUID.randomUUID().toString(),
                                                event.getEventType(),
                                                event.getEvent(), null),
                                        () -> Clock.systemUTC().instant()
                                        ))
                });
    }

    protected <E> EventMessage<? extends E> processInterceptors(EventMessage<E> eventMessage) {
        EventMessage<? extends E> message = eventMessage;

        MessageDispatchInterceptor dispatchInterceptor;
        for(Iterator var3 = this.dispatchInterceptors.iterator(); var3.hasNext(); message = (EventMessage)dispatchInterceptor.handle(message)) {
            dispatchInterceptor = (MessageDispatchInterceptor)var3.next();
        }

        return message;
    }

    public Registration registerDispatchInterceptor(MessageDispatchInterceptor<? super EventMessage<?>> interceptor) {
        this.dispatchInterceptors.add(interceptor);
        return () -> {
            return this.dispatchInterceptors.remove(interceptor);
        };
    }

    public static class Builder extends AbstractEventGateway.Builder {
        private EventBus eventBus;
        private List<MessageDispatchInterceptor<? super EventMessage<?>>> dispatchInterceptors = new CopyOnWriteArrayList();

        public Builder() {
        }

        public GenericDomainEventGateway.Builder eventBus(EventBus eventBus) {
            BuilderUtils.assertNonNull(eventBus, "EventBus may not be null");
            this.eventBus = eventBus;
            return this;
        }

        public GenericDomainEventGateway.Builder dispatchInterceptors(MessageDispatchInterceptor<? super EventMessage<?>>... dispatchInterceptors) {
            this.dispatchInterceptors(Arrays.asList(dispatchInterceptors));
            return this;
        }

        public GenericDomainEventGateway build() {
            return new GenericDomainEventGateway(this);
        }
    }
}
