package com.seidor.store.dto;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;

import com.seidor.store.model.SellDetail;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class SellRequestDTO {

    @NotNull(message = "Es obligatorio al menos un detalle de venta")
    private Set<SellDetailDTO> sellDetails;

    public Set<SellDetailDTO> getSellDetails() {
        return sellDetails;
    }

    public void setSellDetails(Set<SellDetailDTO> sellDetails) {
        this.sellDetails = sellDetails;
    }
}
