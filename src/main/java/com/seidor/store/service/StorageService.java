package com.seidor.store.service;

import com.seidor.store.model.Product;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    public boolean isEnoughStock(Product product,Integer amount) {
        return product.getStorage().getStock() >= amount;
    }

    public void reduceStock(Product product,Integer amount) {
        product.getStorage().setStock(product.getStorage().getStock() - amount);
    }
}

