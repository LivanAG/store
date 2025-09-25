package com.seidor.store.service;

import com.seidor.store.config.AuthUtil;
import com.seidor.store.model.*;
import com.seidor.store.repository.ProductRepository;
import com.seidor.store.repository.SellRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.seidor.store.model.Role.ADMIN;
import static com.seidor.store.model.Role.CLIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SellServiceTest {


    @Mock
    private SellRepository sellRepository;


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
}
