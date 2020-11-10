package axon.services;

import axon.aggregate.OrderAggregate;
import axon.cqrs.query.GetOrderAggregateQuery;
import axon.cqrs.query.dto.OrderAggregateEventsDto;
import axon.projections.OrderAggregateProjection;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OrderQueryService {
    @Autowired
    QueryGateway queryGateway;

    @Autowired
    EventStore storageEngine;

    @Autowired
    EmbeddedEventStore eventStore;

    public OrderAggregate getOrder(GetOrderAggregateQuery query) throws ExecutionException, InterruptedException {
        return queryGateway.query(query, OrderAggregate.class).get();
    }

    public List<OrderAggregateEventsDto> listEventsOfOrder(GetOrderAggregateQuery query) {
        DomainEventStream domainEventStream = storageEngine.readEvents(query.getUuid().toString());
        List<OrderAggregateEventsDto> events = domainEventStream.asStream()
                .map(domainEventMessage -> OrderAggregateEventsDto.builder()
                        .name(domainEventMessage.getPayloadType().getName())
                        .payload(domainEventMessage.getPayload().toString())
                        .sequenceNumber(domainEventMessage.getSequenceNumber())
                        .timestamp(domainEventMessage.getTimestamp())
                        .build())
                .collect(Collectors.toList());
        return events;
    }
}
