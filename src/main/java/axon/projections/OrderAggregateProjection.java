package axon.projections;

import axon.aggregate.OrderAggregate;
import axon.cqrs.query.GetOrderAggregateQuery;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAggregateProjection {
    @Autowired
    EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository;


    @QueryHandler
    public OrderAggregate handle(GetOrderAggregateQuery query) {
        return orderAggregateEventSourcingRepository.load(query.getUuid().toString()).getWrappedAggregate().getAggregateRoot();
    }
}
