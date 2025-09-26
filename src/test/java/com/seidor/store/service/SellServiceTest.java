package com.seidor.store.service;

import com.seidor.store.config.AuthUtil;
import com.seidor.store.dto.SellRequestDTO;
import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.exception.myExceptions.InsufficientStockException;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.model.*;
import com.seidor.store.repository.ProductRepository;
import com.seidor.store.repository.SellRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.seidor.store.model.Role.ADMIN;
import static com.seidor.store.model.Role.CLIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SellServiceTest {


    @Mock
    private SellRepository sellRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;
    @Mock
    private StorageService storageService;


    @InjectMocks
    private SellService sellService;

    @Captor
    ArgumentCaptor<Sell> sellCaptor;


    @Test
    void getAllSells_shouldReturnAllSells_WhenUserIsAdmin() {

        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);

        List<Sell> sells = List.of(new Sell(), new Sell());

        when(sellRepository.findAll()).thenReturn(sells);

        try (MockedStatic<AuthUtil> mockedAuth = Mockito.mockStatic(AuthUtil.class)) {
            mockedAuth.when(AuthUtil::getCurrentUser).thenReturn(adminUser);


            List<Sell> result = sellService.getAllSells();

            assertEquals(2, result.size());
            verify(sellRepository).findAll();
            verify(sellRepository, never()).findByUserId(anyInt());
        }
    }

    @Test
    void getAllSells_shouldReturnClientSells_WhenUserIsClient() {

        final Integer id = 123;

        // ARRANGE
        User clientUser = new User();
        clientUser.setRole(Role.CLIENT);
        clientUser.setId(id);

        List<Sell> clientSells = List.of(new Sell());

        when(sellRepository.findByUserId(id)).thenReturn(clientSells);

        try (MockedStatic<AuthUtil> mockedAuth = Mockito.mockStatic(AuthUtil.class)) {
            mockedAuth.when(AuthUtil::getCurrentUser).thenReturn(clientUser);


            List<Sell> result = sellService.getAllSells();


            assertEquals(1, result.size());
            verify(sellRepository).findByUserId(123);
            verify(sellRepository, never()).findAll();
        }
    }


    @Test
    void addSell_shouldCreateSellWithDetailsAndReduceStock() {


        User user = new User();
        user.setId(1);
        user.setRole(Role.CLIENT);

        Product product = new Product();
        Storage storage = new Storage();
        storage.setPrice(10.0);
        product.setStorage(storage);

        SellDetailDTO detailDto = new SellDetailDTO();
        detailDto.setProductId(100);
        detailDto.setAmount(2);

        SellRequestDTO request = new SellRequestDTO();
        request.setSellDetails(Set.of(detailDto));

        when(productService.getProductById(100)).thenReturn(product);
        when(storageService.isEnoughStock(product, 2)).thenReturn(true);
        when(sellRepository.save(any(Sell.class))).thenAnswer(invocation -> invocation.getArgument(0));


        try (MockedStatic<AuthUtil> mockedAuth = Mockito.mockStatic(AuthUtil.class)) {
            mockedAuth.when(AuthUtil::getCurrentUser).thenReturn(user);

            Sell savedSell = sellService.addSell(request);

            assertEquals(user, savedSell.getUser());
            assertEquals(1, savedSell.getSellDetails().size());
            assertEquals(20.0, savedSell.getTotalSale()); // 10*2


            verify(storageService).reduceStock(product, 2);
            verify(sellRepository).save(any(Sell.class));
        }
    }

    @Test
    void addSell_shouldThrowResourceNotFound_WhenProductDoesNotExist() {

        User user = new User();
        user.setId(1);

        SellDetailDTO detailDto = new SellDetailDTO();
        detailDto.setProductId(999);
        detailDto.setAmount(1);

        SellRequestDTO request = new SellRequestDTO();
        request.setSellDetails(Set.of(detailDto));

        when(productService.getProductById(999))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        try (MockedStatic<AuthUtil> mockedAuth = Mockito.mockStatic(AuthUtil.class)) {
            mockedAuth.when(AuthUtil::getCurrentUser).thenReturn(user);


            assertThrows(ResourceNotFoundException.class, () -> sellService.addSell(request));

            verify(sellRepository, never()).save(any());
        }
    }

    @Test
    void addSell_shouldThrowInsufficientStock_WhenNotEnoughStock() {
        // ARRANGE
        User user = new User();
        user.setId(1);

        Product product = new Product();
        Storage storage = new Storage();
        storage.setPrice(5.0);
        product.setStorage(storage);

        SellDetailDTO detailDto = new SellDetailDTO();
        detailDto.setProductId(100);
        detailDto.setAmount(10);

        SellRequestDTO request = new SellRequestDTO();
        request.setSellDetails(Set.of(detailDto));

        when(productService.getProductById(100)).thenReturn(product);
        when(storageService.isEnoughStock(product, 10)).thenReturn(false);
        when(storageService.getAvailableStock(product)).thenReturn(5);

        try (MockedStatic<AuthUtil> mockedAuth = Mockito.mockStatic(AuthUtil.class)) {
            mockedAuth.when(AuthUtil::getCurrentUser).thenReturn(user);

            // ACT + ASSERT
            assertThrows(InsufficientStockException.class, () -> sellService.addSell(request));

            // verify no reduce stock ni guarda venta
            verify(storageService, never()).reduceStock(any(), anyInt());
            verify(sellRepository, never()).save(any());
        }
    }

    @Test
    void deleteSellById_shouldDelete_WhenSellExists() {
        Integer id = 1;
        Sell sell = new Sell();
        sell.setId(id);

        when(sellRepository.findById(id)).thenReturn(Optional.of(sell));

        sellService.deleteSellbyId(id);

        verify(sellRepository).findById(id);
        verify(sellRepository).deleteById(id);
    }

    @Test
    void deleteSellById_shouldThrow_WhenSellDoesNotExist() {
        Integer id = 99;
        when(sellRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sellService.deleteSellbyId(id));

        verify(sellRepository, never()).deleteById(anyInt());
    }

    @Test
    void getSellById_shouldReturnSell_WhenExists() {
        // ARRANGE
        Integer id = 1;
        Sell sell = new Sell();
        sell.setId(id);

        when(sellRepository.findById(id)).thenReturn(Optional.of(sell));

        // ACT
        Sell result = sellService.getSellById(id);

        // ASSERT
        assertEquals(id, result.getId());
        verify(sellRepository).findById(id);
    }

    @Test
    void getSellById_shouldThrow_WhenNotExists() {
        // ARRANGE
        Integer id = 99;
        when(sellRepository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(ResourceNotFoundException.class, () -> sellService.getSellById(id));
    }

    @Test
    void getSellDetails_shouldReturnDetails_WhenSellExists() {

        Integer id = 1;
        SellDetail detail1 = new SellDetail();
        SellDetail detail2 = new SellDetail();
        Set<SellDetail> details = Set.of(detail1, detail2);

        Sell sell = new Sell();
        sell.setId(id);
        sell.setSellDetails(details);

        when(sellRepository.findById(id)).thenReturn(Optional.of(sell));


        Set<SellDetail> result = sellService.getSellDetails(id);


        assertEquals(2, result.size());
        verify(sellRepository).findById(id);
    }

    @Test
    void getSellDetails_shouldThrow_WhenSellNotExists() {

        Integer id = 99;
        when(sellRepository.findById(id)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> sellService.getSellDetails(id));
    }


}
