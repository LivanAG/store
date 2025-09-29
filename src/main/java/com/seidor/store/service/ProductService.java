package com.seidor.store.service;

import com.seidor.store.dto.productDTOS.ProductRequestDTO;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.mapper.ProductMapper;
import com.seidor.store.model.Product;
import com.seidor.store.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<Product> getAllproducts(){

        return productRepository.findAll();
    }

    public Product getProductById(Integer id){
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product addProduct(ProductRequestDTO request){

        Product product = ProductMapper.toEntity(request);

        productRepository.save(product);

        return product;
    }

    public Product updateProduct(Integer id, ProductRequestDTO request){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());

        productRepository.save(product);
        return product;
    }

    public void deleteProductById(Integer id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.deleteById(id);
    }


    public Product increaseStock(Integer productId, Integer amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        product.getStorage().setStock(product.getStorage().getStock() + amount);
        return productRepository.save(product);
    }

    public Product updatePrice(Integer productId, Double newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        product.getStorage().setPrice(newPrice);
        return productRepository.save(product);
    }




}
