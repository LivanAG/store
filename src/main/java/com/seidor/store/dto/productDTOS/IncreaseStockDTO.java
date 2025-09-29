package com.seidor.store.dto.productDTOS;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncreaseStockDTO {
    @NotNull(message = "El incremento de stock es obligatorio")
    @Positive(message = "El incremento debe ser mayor que cero")
    private Integer amount;
}
