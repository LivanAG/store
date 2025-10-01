package com.seidor.store.dto.sell_dtos;

import com.seidor.store.dto.sell_detail_dtos.SellDetailDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SellRequestDTO {

    @NotEmpty(message = "Es obligatorio al menos un detalle de venta")
    private Set<SellDetailDTO> sellDetails;

}
