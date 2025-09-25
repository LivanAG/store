package com.seidor.store.service;

import com.seidor.store.dto.ProductRequestDTO;
import com.seidor.store.dto.ProductResponseDTO;
import com.seidor.store.dto.storageDTOS.StorageDTO;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.mapper.ProductMapper;
import com.seidor.store.model.Product;
import com.seidor.store.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    ArgumentCaptor<Product> productCaptor;


    @Test
    public void getAllproducts_shouldReturnAll() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);
        List<Product> result = productService.getAllproducts();
        assertEquals(2, result.size());
    }

    //Buscar un producto que no existe debe lanzar la excepcion ResourceNotFoundException
    @Test
    public void getProductById_shouldThrowException_IfNotFound() {
        Integer id = 1;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(id));

        verify(productRepository).findById(id);
    }


    // Agregar un producto valido llama al metodo save del repositorio
    @Test
    public void addProduct_ShouldSaveCorrectProduct(){

        ProductRequestDTO request = new ProductRequestDTO("Livan","descripcion",new StorageDTO(1,10.5));

        productService.addProduct(request);

        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        assertEquals(request.getName(), savedProduct.getName());
        assertEquals(request.getDescription(), savedProduct.getDescription());
        assertNotNull(savedProduct.getStorage());
        assertEquals(request.getStorage().getStock(), savedProduct.getStorage().getStock());
        assertEquals(request.getStorage().getPrice(), savedProduct.getStorage().getPrice());

    }


    //Verico que se llame al metodo save y que los datos se actualizan correctamente
    @Test
    public void updateProduct_ShouldCallSaveMethod_WhenProductIsValid(){
        Integer id = 1;

        Product productOlder = new Product();
        productOlder.setId(id);
        productOlder.setDescription("lalalalala");
        productOlder.setName("Pepe");

        ProductRequestDTO request = new ProductRequestDTO("Livan","descripcion",new StorageDTO(1,10.5));

        when(productRepository.findById(id)).thenReturn(Optional.of(productOlder));

        Product productUpdated = productService.updateProduct(id, request);

        assertEquals(request.getDescription(), productUpdated.getDescription());
        assertEquals(request.getName(), productUpdated.getName());

        verify(productRepository).findById(id);
        verify(productRepository).save(productOlder);

    }

    @Test
    //verifico que se elimina correcatmente un producto
    public void deleteProduct_shouldThrowException_WhenProductExists(){
        Integer id = 1;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,() -> productService.deleteProductById(id));

        verify(productRepository).findById(id);

    }





}
