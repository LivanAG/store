package com.seidor.store.dto.sellDetailDTO;

public class SellDetailDTO {

    private Integer productId;
    private Integer amount;

    public SellDetailDTO(Integer productId, Integer amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
