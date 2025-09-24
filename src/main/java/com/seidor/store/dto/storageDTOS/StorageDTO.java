package com.seidor.store.dto.storageDTOS;

import com.seidor.store.model.Storage;

public class StorageDTO {

    private Integer stock;

    private Double price;

    public StorageDTO(){}
    public StorageDTO(Integer stock, Double price) {
        this.stock = stock;
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
