package com.seidor.store.mapper;

import com.seidor.store.dto.sellDTOS.SellResponseDTO;
import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.model.Sell;

import java.util.Set;
import java.util.stream.Collectors;

public class SellMapper {




    public static SellResponseDTO toDto(Sell sell) {

        Set<SellDetailDTO>detailDTO = sell.getSellDetails().stream()
                .map(SellDetailMapper::toDto)
                .collect(Collectors.toSet());

        return new SellResponseDTO(sell.getId(),sell.getUser().getId(),detailDTO,sell.getTotalSale(),sell.getCreatedAt());
    }

    // De lista de entidades → lista DTO response
    public static java.util.List<SellResponseDTO> toDtoList(java.util.List<Sell> sell) {
        return sell.stream()
                .map(SellMapper::toDto)
                .collect(Collectors.toList());
    }

}
