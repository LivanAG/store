package com.seidor.store.service;

import static org.junit.jupiter.api.Assertions.*;

import com.seidor.store.model.Product;
import com.seidor.store.model.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StorageServiceTest {

    private StorageService storageService;
    private Product product;

    @BeforeEach
    void setUp() {
        storageService = new StorageService();

        Storage storage = new Storage();
        storage.setStock(10);
        product = new Product();
        product.setStorage(storage);
    }

    @Test
    void isEnoughStock_shouldReturnTrue_WhenStockIsSufficient() {
        assertTrue(storageService.isEnoughStock(product, 5));
    }

    @Test
    void isEnoughStock_shouldReturnTrue_WhenStockEqualsAmount() {
        assertTrue(storageService.isEnoughStock(product, 10));
    }

    @Test
    void isEnoughStock_shouldReturnFalse_WhenStockIsInsufficient() {
        assertFalse(storageService.isEnoughStock(product, 15));
    }


    @Test
    void getAvailableStock_shouldReturnCurrentStock() {
        assertEquals(10, storageService.getAvailableStock(product));
    }


    @Test
    void reduceStock_shouldDecreaseStockByAmount() {
        storageService.reduceStock(product, 4);
        assertEquals(6, product.getStorage().getStock());
    }

    @Test
    void reduceStock_shouldAllowReducingToZero() {
        storageService.reduceStock(product, 10);
        assertEquals(0, product.getStorage().getStock());
    }


    @Test
    void increaseStock_shouldIncreaseStockByAmount() {
        storageService.increaseStock(product, 5);
        assertEquals(15, product.getStorage().getStock());
    }

    @Test
    void increaseStock_shouldWorkFromZero() {
        product.getStorage().setStock(0);
        storageService.increaseStock(product, 7);
        assertEquals(7, product.getStorage().getStock());
    }
}
