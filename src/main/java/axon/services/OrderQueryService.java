package axon.services;

import axon.aggregate.order.OrderAggregate;
import axon.cqrs.query.GetOrderAggregateQuery;
import axon.cqrs.query.dto.OrderAggregateEventsDto;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OrderQueryService {
    @Autowired
    QueryGateway queryGateway;

    @Qualifier("OrderStorageEngine")
    @Autowired
    EventStorageEngine orderStorageEngine;

    public OrderAggregate getOrder(GetOrderAggregateQuery query) throws ExecutionException, InterruptedException {
        return queryGateway.query(query, OrderAggregate.class).get();
    }

    public List<OrderAggregateEventsDto> listEventsOfOrder(GetOrderAggregateQuery query) {
        DomainEventStream domainEventStream = orderStorageEngine.readEvents(query.getUuid().toString());
        return domainEventStream.asStream()
                .map(domainEventMessage -> OrderAggregateEventsDto.builder()
                        .name(domainEventMessage.getPayloadType().getName())
                        .payload(domainEventMessage.getPayload().toString())
                        .sequenceNumber(domainEventMessage.getSequenceNumber())
                        .timestamp(domainEventMessage.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
