package com.seidor.store.dto;


import com.seidor.store.dto.storageDTOS.StorageDTO;
import com.seidor.store.model.Sell;
import com.seidor.store.model.SellDetail;

import java.util.List;
import java.util.Set;

public class ProductResponseDTO {

    private String name;
    private String description;
    private Double price;
    private Integer Stock;

    public Integer getStock() {
        return Stock;
    }

    public void setStock(Integer stock) {
        Stock = stock;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
