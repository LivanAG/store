package com.seidor.store.service;

import com.seidor.store.model.Role;
import com.seidor.store.model.User;
import com.seidor.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    private MyUserDetailService userDetailService;

    @BeforeEach
    void setUp() {
        userDetailService = new MyUserDetailService(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_WhenUserExists() {
        // ARRANGE
        String email = "user@test.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPass");
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        UserDetails userDetails = userDetailService.loadUserByUsername(email);


        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_shouldThrowException_WhenUserDoesNotExist() {
        String email = "notfound@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername(email));

        verify(userRepository).findByEmail(email);
    }
}

