package axon.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregateDto {
    private String orderName;
    private double price;
    private List<CreateOrderTransactionAggregateMemberDto> orderTransactions;
}
