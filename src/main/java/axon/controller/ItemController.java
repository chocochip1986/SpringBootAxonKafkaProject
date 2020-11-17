package axon.controller;

import axon.aggregate.item.WeaponAggregate;
import axon.cqrs.query.item.GetItemAggregateQuery;
import axon.cqrs.query.item.GetWeaponAggregateQuery;
import axon.dtos.item.CreateItemAggregateDto;
import axon.dtos.item.UpdateItemAggregateDto;
import axon.services.item.ItemCommandService;
import axon.services.item.ItemQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/item")
public class ItemController {
    @Autowired
    ItemCommandService itemCommandService;

    @Autowired
    ItemQueryService itemQueryService;

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

    @GetMapping(value = "/weapon/{uuid}")
    public ResponseEntity<String> getWeapon(@PathVariable("uuid") String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);

            GetWeaponAggregateQuery query = new GetWeaponAggregateQuery(uuid);
            WeaponAggregate weaponAggregate = itemQueryService.getWeapon(query);
            return new ResponseEntity<>(weaponAggregate.toString(), HttpStatus.OK);
        } catch ( ExecutionException | InterruptedException e ) {
            System.out.println(e.getStackTrace());
            return new ResponseEntity<>("CUI MAN", HttpStatus.BAD_REQUEST);
        }
    }
}
