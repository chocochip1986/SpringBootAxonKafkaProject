package axon.controller;

import axon.aggregate.OrderAggregate;
import axon.cqrs.query.GetOrderAggregateQuery;
import axon.dtos.CreateOrderAggregateDto;
import axon.dtos.UpdateOrderAggregateDto;
import axon.services.OrderCommandService;
import axon.services.OrderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/shootme")
public class Controller {
    @Autowired
    OrderCommandService orderCommandService;

    @Autowired
    OrderQueryService orderQueryService;


    @PostMapping(value = "/order")
    public ResponseEntity<String> createOrder(CreateOrderAggregateDto dto) {
        orderCommandService.createOrder(dto);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @PutMapping(value = "/order")
    public ResponseEntity<String> updateOrder(UpdateOrderAggregateDto dto) {
        orderCommandService.updateOrder(dto);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @GetMapping(value = "/order/${uuid}")
    public ResponseEntity<String> getOrder(@PathVariable UUID uuid) throws ExecutionException, InterruptedException {
        GetOrderAggregateQuery query = new GetOrderAggregateQuery(uuid);
        OrderAggregate orderAggregate;
        try {
            orderAggregate = orderQueryService.getOrder(query);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<String>("JIALAT", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderAggregate.toString(), HttpStatus.OK);
    }
}
