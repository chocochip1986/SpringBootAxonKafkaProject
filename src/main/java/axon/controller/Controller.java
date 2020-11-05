package axon.controller;

import axon.dtos.CreateOrderAggregateDto;
import axon.services.OrderCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shootme")
public class Controller {
    @Autowired
    OrderCommandService orderCommandService;


    @PostMapping(value = "/order")
    public ResponseEntity<String> createOrder(CreateOrderAggregateDto dto) {
        orderCommandService.createOrder(dto);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }
}
