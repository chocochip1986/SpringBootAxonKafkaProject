package axon.dtos.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemAggregateDto {
    private UUID uuid;
    private String name;
    private double damage;
}
