package com.seidor.store.mapper;

import com.seidor.store.dto.sell_detail_dtos.SellDetailDTO;
import com.seidor.store.model.SellDetail;

import java.util.Set;
import java.util.stream.Collectors;

public class SellDetailMapper {

    public static SellDetailDTO toDto(SellDetail sellDetail) {

        return new SellDetailDTO(sellDetail.getProduct().getId(),sellDetail.getAmount(),sellDetail.getProduct().getStorage().getPrice()*sellDetail.getAmount());

    }


    public static Set<SellDetailDTO> toDtoList(Set<SellDetail> sellDetails) {
        return sellDetails.stream().map(SellDetailMapper::toDto).collect(Collectors.toSet());
    }
}
