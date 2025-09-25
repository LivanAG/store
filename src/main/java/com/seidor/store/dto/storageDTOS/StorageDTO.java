package com.seidor.store.dto.storageDTOS;

import com.seidor.store.model.Storage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StorageDTO {

    private Integer stock;

    private Double price;

    public StorageDTO(){}
    public StorageDTO(Integer stock, Double price) {
        this.stock = stock;
        this.price = price;
    }

}
