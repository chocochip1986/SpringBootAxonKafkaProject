package axon.cqrs.query.item;

import axon.aggregate.item.WeaponAggregate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
public class GetItemAggregateQuery<T> {
    private UUID uuid;
    private Class<T> t;

    public GetItemAggregateQuery() {
    }

    public static class CreateQueryForWeaponAggregate {
        public static GetItemAggregateQuery<WeaponAggregate> create(UUID uuid) {
            return new GetItemAggregateQuery<>(uuid, WeaponAggregate.class);
        }
    }
}
