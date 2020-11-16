package axon.events.item;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
public class WeaponAggregateCreatedEvent {
    private UUID uuid;
    private String name;
    private double damage;

    public WeaponAggregateCreatedEvent(UUID uuid, String name, double damage) {
        this.uuid = uuid;
        this.name = name;
        this.damage = damage;
    }
}
