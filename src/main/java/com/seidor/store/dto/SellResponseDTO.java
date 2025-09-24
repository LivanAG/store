package com.seidor.store.dto;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.model.SellDetail;

import java.time.LocalDateTime;
import java.util.Set;

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

    public Integer getSellId() {
        return sellId;
    }

    public void setSellId(Integer sellId) {
        this.sellId = sellId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public Set<SellDetailDTO> getSellDetails() {
        return sellDetails;
    }

    public void setSellDetails(Set<SellDetailDTO> sellDetails) {
        this.sellDetails = sellDetails;
    }

    public Double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(Double totalSale) {
        this.totalSale = totalSale;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
