package axon.cqrs.query.item;

import axon.aggregate.item.ItemAggregate;
import axon.aggregate.item.WeaponAggregate;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemAggregateQueryHandler {
    @Autowired
    private EventSourcingRepository<? extends ItemAggregate> itemAggregateEventSourcingRepository;

    @QueryHandler
    public WeaponAggregate get(GetWeaponAggregateQuery query) {
        return (WeaponAggregate)itemAggregateEventSourcingRepository.load(query.getUuid().toString()).getWrappedAggregate().getAggregateRoot();
    }
}
