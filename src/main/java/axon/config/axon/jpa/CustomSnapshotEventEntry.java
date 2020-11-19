package axon.config.axon.jpa;

import lombok.Data;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.AbstractSnapshotEventEntry;
import org.axonframework.serialization.Serializer;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity(name = "SnapshotEventEntry")
@Table(name = "SNAPSHOT_EVENT_ENTRY")
public class CustomSnapshotEventEntry extends AbstractSnapshotEventEntry<byte[]> {
    public CustomSnapshotEventEntry(DomainEventMessage<?> eventMessage, Serializer serializer, Class<byte[]> contentType) {
        super(eventMessage, serializer, contentType);
    }
}
