package axon.cqrs.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GetOrderAggregateQuery {
    private UUID uuid;
}
