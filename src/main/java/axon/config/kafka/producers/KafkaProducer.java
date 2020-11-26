package axon.config.kafka.producers;

import axon.aggregate.item.WeaponAggregate;
import axon.events.item.WeaponAggregateCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    private ObjectMapper objectMapper;

    @Autowired
    public KafkaProducer() {
        objectMapper = new ObjectMapper();
    }

    @Autowired
    private KafkaMessageConverter<String, byte[]> kafkaMessageConverter;

    public void sendMessageWithReply(String topic, final SerializedObject<byte[]> serializedObject, RecordHeaders headers) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, (Integer) null, null, serializedObject.getData(), headers);

        ListenableFuture<SendResult<String, byte[]>> future =
                kafkaTemplate.send(record);

        future.addCallback(new ListenableFutureCallback<SendResult<String, byte[]>>() {
            public void onFailure(Throwable throwable) {
                System.out.println("Unable to send message due to "+throwable.getMessage());
            }

            public void onSuccess(SendResult<String, byte[]> result) {
                System.out.println("[Thread]: "+Thread.currentThread().getId()+"\n"
                        +"Sent Message with offset = ["+result.getRecordMetadata().offset()+"]");
            }
        });
    }
}
