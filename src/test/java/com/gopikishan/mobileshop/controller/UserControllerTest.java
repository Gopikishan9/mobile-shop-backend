package com.gopikishan.mobileshop.controller;

import com.gopikishan.mobileshop.dto.UserLoginDto;
import com.gopikishan.mobileshop.dto.UserRegisterDto;
import com.gopikishan.mobileshop.dto.UserResponseDto;
import com.gopikishan.mobileshop.exception.DuplicateEmailException;
import com.gopikishan.mobileshop.service.JWTService;
import com.gopikishan.mobileshop.service.ProductService;
import com.gopikishan.mobileshop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService service;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private JWTService jwtService;


    @Test
    void signupShouldReturnOk() throws Exception {
        UserResponseDto response = new UserResponseDto();
        response.setId(1L);
        response.setName("Gopi");
        response.setEmail("gopi@example.com");

        when(service.signup(any(UserRegisterDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Gopi",
                                "email": "gopi@example.com",
                                "password": "Password@123",
                                "confirmPassword": "Password@123"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gopi"))
                .andExpect(jsonPath("$.email").value("gopi@example.com"));

        verify(service).signup(any(UserRegisterDto.class));
    }

    @Test
    void loginShouldReturnTokenAndRole() throws Exception {
        Map<String, String> response = Map.of(
                "token", "mock-jwt-token",
                "role", "ROLE_USER"
        );

        when(service.verify(any(UserLoginDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "gopi@example.com",
                                "password": "password123"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(service).verify(any(UserLoginDto.class));
    }

    @Test
    void signupShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(service.signup(any(UserRegisterDto.class)))
                .thenThrow(new DuplicateEmailException(
                        "User Already exist's with email: gopi@example.com"
                ));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Gopi",
                                "email": "gopi@example.com",
                                "password": "Password@123",
                                "confirmPassword": "Password@123"
                            }
                            """))
                .andExpect(status().isConflict());

        verify(service).signup(any(UserRegisterDto.class));
    }

    @Test
    void loginShouldPropagateBadCredentialsException() throws Exception {
        when(service.verify(any(UserLoginDto.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "gopi@example.com",
                                "password": "wrongPassword"
                            }
                            """))
                .andExpect(status().isUnauthorized());

        verify(service).verify(any(UserLoginDto.class));
    }

    @Test
    void signupShouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "",
                                "email": "gopi@example.com",
                                "password": "password123",
                                "confirmPassword": "password123"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void signupShouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Gopi",
                                "email": "invalid-email",
                                "password": "password123",
                                "confirmPassword": "password123"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void signupShouldReturnBadRequestWhenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Gopi",
                                "email": "gopi@example.com",
                                "password": "",
                                "confirmPassword": "password123"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void loginShouldReturnBadRequestWhenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "",
                                "password": "password123"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }



}