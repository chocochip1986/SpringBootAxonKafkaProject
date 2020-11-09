package axon.config.axon;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class CommandMessageInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {
        return (index, command) -> {
            System.out.println("Index: " +index+") Dispatching command { "+command+" }");
            return command;
        };
    }
}
