package com.seidor.store.dto.sellDetailDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
