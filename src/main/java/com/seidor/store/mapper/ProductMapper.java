package com.seidor.store.mapper;

import com.seidor.store.dto.product_dtos.ProductRequestDTO;
import com.seidor.store.dto.product_dtos.ProductResponseDTO;
import com.seidor.store.dto.storage_dtos.StorageDTO;
import com.seidor.store.model.Product;
import com.seidor.store.model.Storage;

import java.util.stream.Collectors;

public class ProductMapper {

    public static Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());

        StorageDTO incoming = dto.getStorage();

        Storage storage = new Storage();
        storage.setPrice(incoming.getPrice());
        storage.setStock(incoming.getStock());

        product.setStorage(storage);
        return product;
    }


    public static ProductResponseDTO toDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getStorage().getPrice());
        dto.setStock(product.getStorage().getStock());
        return dto;
    }

    // De lista de entidades → lista DTO response
    public static java.util.List<ProductResponseDTO> toDtoList(java.util.List<Product> products) {
        return products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
}
