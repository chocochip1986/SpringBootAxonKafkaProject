package axon.services.item;

import axon.aggregate.item.WeaponAggregate;
import axon.cqrs.query.item.GetWeaponAggregateQuery;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ItemQueryService {
    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private EmbeddedEventStore itemEventStore;

    public WeaponAggregate getWeapon(GetWeaponAggregateQuery query) throws ExecutionException, InterruptedException {
        return this.queryGateway.query(query, WeaponAggregate.class).get();
    }
}
