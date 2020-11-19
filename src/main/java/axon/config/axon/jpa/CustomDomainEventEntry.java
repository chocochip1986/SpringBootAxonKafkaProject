package axon.config.axon.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.eventhandling.AbstractSequencedDomainEventEntry;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.serialization.Serializer;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "DomainEventEntry")
@Data
@Table(indexes = {@Index(
        columnList = "aggregateIdentifier,sequenceNumber",
        unique = true)},
        name = "DOMAIN_EVENT_ENTRY"
        )
@NoArgsConstructor
public class CustomDomainEventEntry extends AbstractSequencedDomainEventEntry<byte[]> {
    public CustomDomainEventEntry(DomainEventMessage<?>eventMessage, Serializer serializer) {
        super(eventMessage, serializer, byte[].class);
    }
}
