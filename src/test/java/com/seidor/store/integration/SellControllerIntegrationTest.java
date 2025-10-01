package com.seidor.store.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.seidor.store.dto.sell_dtos.SellRequestDTO;
import com.seidor.store.dto.sell_detail_dtos.SellDetailDTO;
import com.seidor.store.model.Product;
import com.seidor.store.model.Storage;
import com.seidor.store.model.User;
import com.seidor.store.model.Role;
import com.seidor.store.repository.ProductRepository;
import com.seidor.store.repository.UserRepository;
import com.seidor.store.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SellControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;

    private Product testProduct;

    @BeforeEach
    void setup() {
        // Crear usuario de prueba si no existe
            testUser = userRepository.findByEmail("livan@email.com").orElseGet(() -> {
            User u = new User();
            u.setUsername("testuser");
            u.setPassword("Secreto123");
            u.setEmail("livan@email.com");
            u.setRole(Role.ADMIN);
            return userRepository.save(u);
        });

        // Crear producto de prueba
        testProduct = productRepository.findById(1).orElseGet(() -> {
            Product p = new Product();
            p.setName("ProductoTest");
            Storage storage = new Storage();
            storage.setPrice(100.0);
            storage.setStock(50);
            p.setStorage(storage);
            return productRepository.save(p);
        });
    }


    private String generateTokenForTestUser() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "livan@email.com",
                "Secreto123",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        return jwtUtil.generateToken(userDetails);
    }
    @Test
    void addSell_shouldCreateSell() throws Exception {
        String token = generateTokenForTestUser();

        SellRequestDTO request = new SellRequestDTO();
        SellDetailDTO detail = new SellDetailDTO();

        detail.setProductId(testProduct.getId());
        detail.setAmount(5);
        request.setSellDetails(Set.of(detail));

        mockMvc.perform(post("/sell")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSale").value(500.0))
                .andExpect(jsonPath("$.sellDetails[0].amount").value(5));
    }

    @Test
    void getAllSells_shouldReturnList() throws Exception {
        String token = generateTokenForTestUser();

        mockMvc.perform(get("/sell")
                        .header("Authorization", "Bearer " + token))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSellById_shouldReturnSell() throws Exception {
        String token = generateTokenForTestUser();
        // Crear venta primero
        SellRequestDTO request = new SellRequestDTO();
        SellDetailDTO detail = new SellDetailDTO();
        detail.setProductId(testProduct.getId());
        detail.setAmount(2);
        request.setSellDetails(Set.of(detail));

        String response = mockMvc.perform(post("/sell")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Integer sellId = objectMapper.readTree(response).get("sellId").asInt();

        mockMvc.perform(get("/sell/" + sellId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellId").value(sellId))
                .andExpect(jsonPath("$.sellDetails[0].amount").value(detail.getAmount()));
    }


    @Test
    void addSell_shouldReturnBadRequest_WhenSellDetailsEmpty() throws Exception {
        String token = generateTokenForTestUser();
        SellRequestDTO request = new SellRequestDTO();
        request.setSellDetails(Set.of());

        mockMvc.perform(post("/sell")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void addSell_shouldReturnError_WhenStockInsufficient() throws Exception {
        String token = generateTokenForTestUser();

        // Crear un producto con stock limitado
        Product limitedProduct = productRepository.findById(2).orElseGet(() -> {
            Product p = new Product();
            p.setName("ProductoLimitado");
            Storage storage = new Storage();
            storage.setPrice(50.0);
            storage.setStock(3); // Stock insuficiente para la venta que vamos a simular
            p.setStorage(storage);
            return productRepository.save(p);
        });

        SellRequestDTO request = new SellRequestDTO();
        SellDetailDTO detail = new SellDetailDTO();
        detail.setProductId(limitedProduct.getId());
        detail.setAmount(5); // Intentamos vender más del stock disponible
        request.setSellDetails(Set.of(detail));

        mockMvc.perform(post("/sell")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError()); // Bad Request o Conflict según tu manejo
    }

    @Test
    void addSell_shouldCreateSellWithMultipleProducts_andCalculateTotalSale() throws Exception {
        String token = generateTokenForTestUser();

        // Crear dos productos de prueba
        Product product1 = productRepository.findById(3).orElseGet(() -> {
            Product p = new Product();
            p.setName("Producto1");
            Storage storage = new Storage();
            storage.setPrice(10.0);
            storage.setStock(50);
            p.setStorage(storage);
            return productRepository.save(p);
        });

        Product product2 = productRepository.findById(4).orElseGet(() -> {
            Product p = new Product();
            p.setName("Producto2");
            Storage storage = new Storage();
            storage.setPrice(20.0);
            storage.setStock(50);
            p.setStorage(storage);
            return productRepository.save(p);
        });

        SellRequestDTO request = new SellRequestDTO();

        SellDetailDTO detail1 = new SellDetailDTO();
        detail1.setProductId(product1.getId());
        detail1.setAmount(2); // 2 * 10 = 20

        SellDetailDTO detail2 = new SellDetailDTO();
        detail2.setProductId(product2.getId());
        detail2.setAmount(3); // 3 * 20 = 60

        request.setSellDetails(Set.of(detail1, detail2));

        mockMvc.perform(post("/sell")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSale").value(80.0)) // 20 + 60 = 80
                .andExpect(jsonPath("$.sellDetails.length()").value(2))
                .andExpect(jsonPath("$.sellDetails[?(@.productId==" + product1.getId() + ")].amount").value(2))
                .andExpect(jsonPath("$.sellDetails[?(@.productId==" + product2.getId() + ")].amount").value(3));
    }

}
