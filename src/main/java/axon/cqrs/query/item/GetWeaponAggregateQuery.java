package axon.cqrs.query.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GetWeaponAggregateQuery {
    private UUID uuid;
}
