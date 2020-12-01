package axon.services;

import org.axonframework.eventhandling.EventMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AxonKafkaService {

    @Transactional
    public void persist(List<? extends EventMessage<?>> eventMessages) {

    }
}
