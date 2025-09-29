package com.seidor.store.dto.sellDTOS;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;

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
