package com.seidor.store.service;

import com.seidor.store.config.AuthUtil;
import com.seidor.store.dto.ProductRequestDTO;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.mapper.ProductMapper;
import com.seidor.store.model.Product;
import com.seidor.store.model.User;
import com.seidor.store.repository.ProductRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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






}
