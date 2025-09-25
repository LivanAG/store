package com.seidor.store.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.seidor.store.dto.SellRequestDTO;
import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
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
public class SellControllerIntegrationTest {

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
}
