package com.seidor.store.dto;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;

import com.seidor.store.model.SellDetail;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SellRequestDTO {

    @NotNull(message = "Es obligatorio al menos un detalle de venta")
    private Set<SellDetailDTO> sellDetails;

}
