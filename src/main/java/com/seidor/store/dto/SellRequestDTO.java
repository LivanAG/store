package com.seidor.store.dto;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;

import com.seidor.store.model.SellDetail;

import java.util.Set;

public class SellRequestDTO {

    private Set<SellDetailDTO> sellDetails;

    public Set<SellDetailDTO> getSellDetails() {
        return sellDetails;
    }

    public void setSellDetails(Set<SellDetailDTO> sellDetails) {
        this.sellDetails = sellDetails;
    }
}
