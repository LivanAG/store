package com.seidor.store.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.seidor.store.dto.ProductRequestDTO;
import com.seidor.store.dto.ProductResponseDTO;
import com.seidor.store.dto.storageDTOS.StorageDTO;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.model.Product;
import com.seidor.store.model.Storage;
import com.seidor.store.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        Storage  storage = new Storage();
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product1");
        product1.setStorage(storage);

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product2");
        product2.setStorage(storage);

        when(productService.getAllproducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Product1"))
                .andExpect(jsonPath("$[1].name").value("Product2"));

        verify(productService).getAllproducts();
    }

    @Test
    void getProductById_shouldReturnProduct_WhenExists() throws Exception {
        Storage  storage = new Storage();
        Product product = new Product();
        product.setId(1);
        product.setName("Product1");
        product.setStorage(storage);

        when(productService.getProductById(1)).thenReturn(product);

        mockMvc.perform(get("/product/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product1"));

        verify(productService).getProductById(1);
    }

    @Test
    void getProductById_shouldReturnNotFound_WhenDoesNotExist() throws Exception {
        when(productService.getProductById(1)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/product/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(1);
    }

    @Test
    void addProduct_shouldReturnCreatedProduct() throws Exception {


        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setStock(10);
        storageDTO.setPrice(10.5);

        Storage storage = new Storage();
        storage.setStock(storageDTO.getStock());
        storage.setPrice(storageDTO.getPrice());

        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("New Product");
        request.setDescription("Description");
        request.setStorage(storageDTO);


        Product savedProduct = new Product();
        savedProduct.setName(request.getName());
        savedProduct.setDescription(request.getDescription());
        savedProduct.setStorage(storage);

        when(productService.addProduct(any(ProductRequestDTO.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()));

        verify(productService).addProduct(any(ProductRequestDTO.class));
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {

        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setStock(10);
        storageDTO.setPrice(10.5);

        Storage storage = new Storage();
        storage.setStock(storageDTO.getStock());
        storage.setPrice(storageDTO.getPrice());

        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setStorage(storageDTO);

        Product updatedProduct = new Product();
        updatedProduct.setName(request.getName());
        updatedProduct.setDescription(request.getDescription());
        updatedProduct.setStorage(storage);

        when(productService.updateProduct(eq(1), any(ProductRequestDTO.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));

        verify(productService).updateProduct(eq(1), any(ProductRequestDTO.class));
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProductById(1);

        mockMvc.perform(delete("/product/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProductById(1);
    }
}

