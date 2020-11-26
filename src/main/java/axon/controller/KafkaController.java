package axon.controller;

import axon.aggregate.item.WeaponAggregate;
import axon.config.kafka.producers.KafkaProducer;
import axon.events.item.WeaponAggregateCreatedEvent;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaController {
    @Autowired
    KafkaProducer kafkaProducer;

    @PostMapping(value = "/fire")
    public ResponseEntity<String> publishSomething(){
        WeaponAggregateCreatedEvent event =
                WeaponAggregateCreatedEvent.builder()
                        .uuid(UUID.randomUUID())
                        .damage(32311)
                        .name("Test")
                        .build();
        RecordHeaders headers = new RecordHeaders();
        headers.add("axon-message-id", UUID.randomUUID().toString().getBytes());
        headers.add("axon-message-type", WeaponAggregateCreatedEvent.class.toString().getBytes());
        headers.add("axon-message-aggregate-id", event.getUuid().toString().getBytes());
        headers.add("axon-message-aggregate-type", WeaponAggregate.class.getName().getBytes());
        headers.add("axon-message-aggregate-seq", ByteBuffer.allocate(Long.BYTES).putLong(0l).array());
        headers.add("axon-message-timestamp", LocalDateTime.now().toString().getBytes());


        JacksonSerializer jacksonSerializer = JacksonSerializer.builder().build();
        SerializedObject<byte[]> serializedObject = jacksonSerializer.serialize(event, byte[].class);

        kafkaProducer.sendMessageWithReply("local.event", serializedObject, headers);
        return new ResponseEntity<>("Sounds ok", HttpStatus.OK);
    }
}
