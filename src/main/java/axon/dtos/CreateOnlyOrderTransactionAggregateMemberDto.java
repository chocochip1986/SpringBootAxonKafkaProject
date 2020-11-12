package axon.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOnlyOrderTransactionAggregateMemberDto {
    private UUID orderUuid;
    private double amount;
}
