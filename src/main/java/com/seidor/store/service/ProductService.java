package com.seidor.store.service;

import com.seidor.store.dto.product_dtos.ProductRequestDTO;
import com.seidor.store.exception.my_exceptions.ResourceNotFoundException;
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
        Product product = this.getProductById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());

        productRepository.save(product);
        return product;
    }

    public void deleteProductById(Integer id){
        this.getProductById(id);
        productRepository.deleteById(id);
    }


    public Product increaseStock(Integer id, Integer amount) {
        Product product = this.getProductById(id);

        product.getStorage().setStock(product.getStorage().getStock() + amount);
        return productRepository.save(product);
    }

    public Product updatePrice(Integer id, Double newPrice) {
        Product product = this.getProductById(id);

        product.getStorage().setPrice(newPrice);
        return productRepository.save(product);
    }




}
