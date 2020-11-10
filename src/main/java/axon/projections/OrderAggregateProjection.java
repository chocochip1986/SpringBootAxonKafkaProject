package axon.projections;

import lombok.Data;
import java.util.HashMap;
import java.util.UUID;

@Data
public class OrderAggregateProjection {
    private UUID uuid;
    private String orderName;
    private double price;

    private HashMap<UUID, Double> orderTransactions;
}
