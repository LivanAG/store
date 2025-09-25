package com.seidor.store.dto;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.model.SellDetail;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class SellResponseDTO {

    private Integer sellId;
    private Integer userId;
    private Set<SellDetailDTO> sellDetails;
    private Double totalSale;
    private LocalDateTime createdAt;

    public SellResponseDTO(Integer sellId,Integer userId,Set<SellDetailDTO> sellDetails, Double totalSale, LocalDateTime createdAt) {
        this.sellId = sellId;
        this.userId = userId;
        this.sellDetails = sellDetails;
        this.totalSale = totalSale;
        this.createdAt = createdAt;
    }


}
