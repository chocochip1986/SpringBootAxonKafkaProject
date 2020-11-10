package axon.cqrs.query;


import axon.aggregate.OrderAggregate;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderAggregateQueryHandler {
    @Autowired
    private EventSourcingRepository<OrderAggregate> orderAggregateEventSourcingRepository;

    @QueryHandler
    public OrderAggregate retrieveOrderAggregate(GetOrderAggregateQuery query) {
        return orderAggregateEventSourcingRepository.load(query.getUuid().toString()).getWrappedAggregate().getAggregateRoot();
    }
}
