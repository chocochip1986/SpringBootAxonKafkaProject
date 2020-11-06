package axon.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderAggregateDto {
    private UUID uuid;
    private String orderName;
    private double price;
}
