package axon.controller;

import axon.dtos.item.CreateItemAggregateDto;
import axon.dtos.item.UpdateItemAggregateDto;
import axon.services.item.ItemCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/item")
public class ItemController {
    @Autowired
    ItemCommandService itemCommandService;

    @PostMapping(value = "/weapon")
    public ResponseEntity<String> createWeapon(@RequestBody CreateItemAggregateDto dto) {
        itemCommandService.createWeapon(dto);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PutMapping(value = "/weapon")
    public ResponseEntity<String> updateWeapon(@RequestBody UpdateItemAggregateDto dto) {
        itemCommandService.updateWeapon(dto);
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
