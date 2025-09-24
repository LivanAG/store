package com.seidor.store.dto;

import com.seidor.store.dto.storageDTOS.StorageDTO;
import com.seidor.store.model.Storage;
import jakarta.validation.constraints.NotBlank;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public StorageDTO getStorage() {
        return storage;
    }

    public void setStorage(StorageDTO storage) {
        this.storage = storage;
    }
}
