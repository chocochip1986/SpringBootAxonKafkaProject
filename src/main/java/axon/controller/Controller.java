package axon.controller;

import axon.aggregate.OrderAggregate;
import axon.cqrs.query.GetOrderAggregateQuery;
import axon.dtos.CreateOrderAggregateDto;
import axon.dtos.UpdateOrderAggregateDto;
import axon.services.OrderCommandService;
import axon.services.OrderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/shootme")
public class Controller {
    @Autowired
    OrderCommandService orderCommandService;

    @Autowired
    OrderQueryService orderQueryService;


    @PostMapping(value = "/order")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderAggregateDto dto) {
//        try {
            return new ResponseEntity<>(orderCommandService.createOrder(dto).toString(), HttpStatus.OK);
//        } catch ( ExecutionException | InterruptedException e ) {
//            System.out.println("Jialat la got problem creating Order!");
//            return new ResponseEntity<String>("Jialat la got problem creating Order!", HttpStatus.BAD_REQUEST);
//        }
    }

    @PutMapping(value = "/order", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateOrder(@RequestBody UpdateOrderAggregateDto dto) {
        orderCommandService.updateOrder(dto);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @GetMapping(value = "/order/{uuid}")
    public ResponseEntity<String> getOrder(@PathVariable("uuid") String uuidString) throws ExecutionException, InterruptedException {
        UUID uuid = UUID.fromString(uuidString);
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
