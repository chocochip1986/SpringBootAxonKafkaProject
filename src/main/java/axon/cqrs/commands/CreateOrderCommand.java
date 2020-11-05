package axon.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderCommand {
    private String orderName;
    private double price;
}
