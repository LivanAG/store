package com.seidor.store.dto;


import com.seidor.store.dto.storageDTOS.StorageDTO;
import com.seidor.store.model.Sell;
import com.seidor.store.model.SellDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class ProductResponseDTO {

    private String name;
    private String description;
    private Double price;
    private Integer Stock;


}
