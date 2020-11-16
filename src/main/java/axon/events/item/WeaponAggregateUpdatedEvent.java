package axon.events.item;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
public class WeaponAggregateUpdatedEvent {
    private String name;
    private double damage;

    public WeaponAggregateUpdatedEvent(String name, double damage) {
        this.name = name;
        this.damage = damage;
    }
}
