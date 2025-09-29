package com.seidor.store.dto.storageDTOS;

import com.seidor.store.model.Storage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StorageDTO {

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser mayor que cero")
    private Integer stock;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double price;

    public StorageDTO(){}
    public StorageDTO(Integer stock, Double price) {
        this.stock = stock;
        this.price = price;
    }

}
