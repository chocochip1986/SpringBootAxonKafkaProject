package axon.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderTransactionCommand {
    private UUID transactionId;
    private double amount;

    public CreateOrderTransactionCommand(double amount) {
        transactionId = UUID.randomUUID();
        this.amount = amount;
    }
}
