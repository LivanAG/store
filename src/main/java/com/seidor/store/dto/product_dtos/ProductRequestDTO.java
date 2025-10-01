package com.seidor.store.dto.product_dtos;

import com.seidor.store.dto.storage_dtos.StorageDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "El name es obligatorio")
    private String name;


    private String description;

    @Valid
    @NotNull(message = "El storage es obligatorio")
    private StorageDTO storage;

}
