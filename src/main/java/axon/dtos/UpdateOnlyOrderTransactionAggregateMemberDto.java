package axon.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOnlyOrderTransactionAggregateMemberDto {
    private UUID orderUuid;
    private UUID orderTransactionUuid;
    private double amount;
}
