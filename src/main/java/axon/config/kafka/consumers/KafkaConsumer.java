package axon.config.kafka.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "local.event.2",
            groupId = "group-id-2")
    public void consume(String message) {
        String finalMsg = "[Thead]: "+Thread.currentThread().getId()+"\n";
        finalMsg += "[Message]: "+message;
        System.out.println(finalMsg);
    }
}
