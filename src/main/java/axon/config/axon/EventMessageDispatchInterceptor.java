package axon.config.axon;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class EventMessageDispatchInterceptor implements MessageDispatchInterceptor<EventMessage<?>> {

    @Override
    public BiFunction<Integer, EventMessage<?>, EventMessage<?>> handle(List<? extends EventMessage<?>> list) {
        return (index, event) -> {
            System.out.println("Index: " +index+") Dispatching event { "+event+" }");
            return event;
        };
    }
}
