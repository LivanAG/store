package com.seidor.store.dto.productDTOS;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDTO {

    private String name;
    private String description;
    private Double price;
    private Integer Stock;


}
