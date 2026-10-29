package com.gopikishan.mobileshop.service;

import com.gopikishan.mobileshop.entity.User;
import com.gopikishan.mobileshop.entity.UserPrincipal;
import com.gopikishan.mobileshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        IO.println("set up");
        // Mockito initializes the mock and injects it automatically.
    }


    @Test
    void loadUserByUsernameShouldReturnUserDetails() {
        User user = new User();
        user.setEmail("gopi@example.com");

        when(repository.findByEmail("gopi@example.com"))
                .thenReturn(java.util.Optional.of(user));

        UserDetails result =
                customUserDetailsService.loadUserByUsername("gopi@example.com");

        assertNotNull(result);
        verify(repository).findByEmail("gopi@example.com");
    }


    @Test
    void loadUserByUsernameShouldReturnUserPrincipal() {
        User user = new User();
        user.setEmail("gopi@example.com");

        when(repository.findByEmail("gopi@example.com"))
                .thenReturn(java.util.Optional.of(user));

        UserDetails result =
                customUserDetailsService.loadUserByUsername("gopi@example.com");

        assertInstanceOf(UserPrincipal.class, result);
    }


    @Test
    void loadUserByUsernameShouldThrowExceptionWhenUserNotFound() {
        String email = "missing@example.com";

        when(repository.findByEmail(email))
                .thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );

        assertEquals("User not found " + email, exception.getMessage());
    }


    @Test
    void loadUserByUsernameShouldCallRepositoryWithCorrectEmail() {
        String email = "gopi@example.com";

        User user = new User();
        user.setEmail(email);

        when(repository.findByEmail(email))
                .thenReturn(java.util.Optional.of(user));

        customUserDetailsService.loadUserByUsername(email);

        verify(repository, times(1)).findByEmail(email);
    }



}