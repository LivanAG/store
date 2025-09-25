package com.seidor.store.controller;


import com.seidor.store.dto.ProductRequestDTO;
import com.seidor.store.dto.ProductResponseDTO;
import com.seidor.store.mapper.ProductMapper;
import com.seidor.store.model.Product;
import com.seidor.store.service.ProductService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return  ResponseEntity.ok(ProductMapper.toDtoList(productService.getAllproducts()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ProductMapper.toDto(productService.getProductById(id)));

    }


    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(ProductMapper.toDto(productService.addProduct(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable("id") Integer id,@Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(ProductMapper.toDto(productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }





}
