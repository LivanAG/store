package com.seidor.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seidor.store.dto.sell_dtos.SellRequestDTO;
import com.seidor.store.dto.sell_detail_dtos.SellDetailDTO;
import com.seidor.store.model.Product;
import com.seidor.store.model.Sell;
import com.seidor.store.model.SellDetail;
import com.seidor.store.model.Storage;
import com.seidor.store.model.User;
import com.seidor.store.service.SellService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SellController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SellControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellService sellService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllSells_shouldReturnListOfSells() throws Exception {
        Sell sell = new Sell();
        sell.setId(1);


        User user = new User();
        user.setId(100);
        sell.setUser(user);

        when(sellService.getAllSells()).thenReturn(List.of(sell));

        mockMvc.perform(get("/sell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sellId").value(1))
                .andExpect(jsonPath("$[0].userId").value(100));

        verify(sellService).getAllSells();
    }

    @Test
    void getSellById_shouldReturnSell() throws Exception {
        Sell sell = new Sell();
        sell.setId(1);

        User user = new User();
        user.setId(100);
        sell.setUser(user);

        when(sellService.getSellById(1)).thenReturn(sell);

        mockMvc.perform(get("/sell/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellId").value(1));

        verify(sellService).getSellById(1);
    }

    @Test
    void getSellDetails_shouldReturnSellDetails() throws Exception {

        Product product = new Product();
        product.setId(1);
        Storage storage = new Storage();
        storage.setPrice(10.0);
        product.setStorage(storage);

        SellDetail detail = new SellDetail();
        detail.setProduct(product);
        detail.setAmount(2);

        Set<SellDetail> details = new HashSet<>();
        details.add(detail);

        when(sellService.getSellDetails(1)).thenReturn(details);

        mockMvc.perform(get("/sell/sell-details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(product.getId()))
                .andExpect(jsonPath("$[0].amount").value(detail.getAmount()))
                .andExpect(jsonPath("$[0].amountTotal").value(detail.getAmount()*product.getStorage().getPrice()));
        verify(sellService).getSellDetails(1);
    }

    @Test
    void addSell_shouldReturnSell() throws Exception {

        User user = new User();
        user.setId(100);

        SellRequestDTO request = new SellRequestDTO();
        SellDetailDTO detailDTO = new SellDetailDTO();
        detailDTO.setProductId(1);
        detailDTO.setAmount(2);
        request.setSellDetails(Set.of(detailDTO));

        Sell sell = new Sell();
        sell.setId(1);
        sell.setSellDetails(new HashSet<>());
        sell.setUser(user);

        when(sellService.addSell(any(SellRequestDTO.class))).thenReturn(sell);

        mockMvc.perform(post("/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellId").value(sell.getId()))
                .andExpect(jsonPath("$.userId").value(sell.getUser().getId()));

        verify(sellService).addSell(any(SellRequestDTO.class));
    }

    @Test
    void deleteSell_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/sell/1"))
                .andExpect(status().isNoContent());

        verify(sellService).deleteSellbyId(1);
    }
}
