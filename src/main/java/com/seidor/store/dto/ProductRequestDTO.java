package com.seidor.store.dto;

import com.seidor.store.dto.storageDTOS.StorageDTO;
import com.seidor.store.model.Storage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductRequestDTO {

    @NotBlank(message = "El name es obligatorio")
    private String name;

    private String description;

    private StorageDTO storage;

    public ProductRequestDTO(){}

    public ProductRequestDTO(String name, String description, StorageDTO storage) {

        this.name = name;
        this.description = description;
        this.storage = storage;
    }


}
