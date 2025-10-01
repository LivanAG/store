package com.seidor.store.dto.sell_detail_dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SellDetailDTO {

    private Integer productId;
    private Integer amount;
    private Double amountTotal;

    public SellDetailDTO(Integer productId, Integer amount,Double amountTotal) {
        this.productId = productId;
        this.amount = amount;
        this.amountTotal = amountTotal;
    }

}
