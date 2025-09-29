package com.seidor.store.dto.productDTOS;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePriceDTO {
    @NotNull(message = "El nuevo precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double newPrice;

}
