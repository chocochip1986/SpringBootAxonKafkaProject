package axon.services;

import axon.aggregate.OrderAggregate;
import axon.cqrs.query.GetOrderAggregateQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class OrderQueryService {
    @Autowired
    QueryGateway queryGateway;

    public OrderAggregate getOrder(GetOrderAggregateQuery query) throws ExecutionException, InterruptedException {
        return queryGateway.query(query, OrderAggregate.class).get();
    }
}
