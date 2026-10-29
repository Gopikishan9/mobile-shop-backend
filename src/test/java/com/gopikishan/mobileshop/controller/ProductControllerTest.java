package com.gopikishan.mobileshop.controller;

import com.gopikishan.mobileshop.dto.ProductRequestDto;
import com.gopikishan.mobileshop.dto.ProductResponseDto;
import com.gopikishan.mobileshop.dto.ProductUpdateDto;
import com.gopikishan.mobileshop.exception.ProductNotFoundException;
import com.gopikishan.mobileshop.service.JWTService;
import com.gopikishan.mobileshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;
    @MockitoBean
    private JWTService jwtService;


    @Test
    void addProductShouldReturnCreated() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "iphone.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        ProductResponseDto response = new ProductResponseDto();
        response.setId(1L);
        response.setName("iPhone 16");
        response.setBrand("Apple");
        response.setImage("https://example.com/iphone.jpg");

        when(service.addProduct(any(ProductRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/products")
                        .file(image)
                        .param("name", "iPhone 16")
                        .param("brand", "Apple")
                        .param("price", "75000")
                        .param("oldPrice", "80000")
                        .param("category", "Smartphone")
                        .param("inStock", "true")
                        .param("description", "Apple smartphone")
                        .param("specifications", "{\"RAM\":\"8GB\",\"Storage\":\"128GB\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 16"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.image").value("https://example.com/iphone.jpg"));

        verify(service).addProduct(any(ProductRequestDto.class));
    }

    @Test
    void getAllProductsShouldReturnOk() throws Exception {
        ProductResponseDto product1 = new ProductResponseDto();
        product1.setId(1L);
        product1.setName("iPhone 16");
        product1.setBrand("Apple");
        product1.setImage("https://example.com/iphone.jpg");

        ProductResponseDto product2 = new ProductResponseDto();
        product2.setId(2L);
        product2.setName("Samsung S25");
        product2.setBrand("Samsung");
        product2.setImage("https://example.com/samsung.jpg");

        when(service.getAllProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("iPhone 16"))
                .andExpect(jsonPath("$[0].image")
                        .value("https://example.com/iphone.jpg"))
                .andExpect(jsonPath("$[1].name").value("Samsung S25"))
                .andExpect(jsonPath("$[1].image")
                        .value("https://example.com/samsung.jpg"));

        verify(service).getAllProducts();
    }

    @Test
    void updateProductShouldReturnOk() throws Exception {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(1L);
        response.setName("iPhone 16 Pro");
        response.setBrand("Apple");
        response.setPrice(85000L);
        response.setImage("https://example.com/iphone.jpg");

        when(service.updateProduct(eq(1L), any(ProductUpdateDto.class)))
                .thenReturn(response);

        MockMultipartFile image = new MockMultipartFile(
                "image", "iphone.jpg", "image/jpeg", "image".getBytes()
        );

        mockMvc.perform(multipart("/api/products/1")
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("name", "iPhone 16 Pro")
                        .param("price", "85000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 16 Pro"))
                .andExpect(jsonPath("$.price").value(85000))
                .andExpect(jsonPath("$.image")
                        .value("https://example.com/iphone.jpg"));

        verify(service).updateProduct(eq(1L), any(ProductUpdateDto.class));
    }

    @Test
    void deleteProductShouldReturnNoContent() throws Exception {
        doNothing().when(service).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(service).deleteProduct(1L);
    }

    @Test
    void getProductByIdShouldReturnNotFound() throws Exception {
        when(service.getProductById(99L))
                .thenThrow(new ProductNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());

        verify(service).getProductById(99L);
    }

    @Test
    void updateProductShouldReturnNotFound() throws Exception {
        when(service.updateProduct(eq(99L), any(ProductUpdateDto.class)))
                .thenThrow(new ProductNotFoundException("Product not found with id: 99"));

        mockMvc.perform(multipart("/api/products/99")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("name", "iPhone 16 Pro")
                        .param("price", "85000"))
                .andExpect(status().isNotFound());

        verify(service).updateProduct(eq(99L), any(ProductUpdateDto.class));
    }

    @Test
    void deleteProductShouldReturnNotFound() throws Exception {
        doThrow(new ProductNotFoundException("Product not found with id: 99"))
                .when(service).deleteProduct(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());

        verify(service).deleteProduct(99L);
    }

    @Test
    void addProductShouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "iphone.jpg",
                "image/jpeg",
                "image".getBytes()
        );

        mockMvc.perform(multipart("/api/products")
                        .file(image)
                        .param("name", "")
                        .param("brand", "Apple")
                        .param("price", "75000")
                        .param("oldPrice", "80000")
                        .param("category", "Smartphone")
                        .param("inStock", "true")
                        .param("description", "Apple smartphone")
                        .param("specifications", "{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).addProduct(any(ProductRequestDto.class));
    }

    @Test
    void updateProductShouldCallServiceWithUpdateRequest() throws Exception {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(1L);
        response.setName("iPhone 16 Pro");
        response.setBrand("Apple");
        response.setPrice(85000L);
        response.setImage("https://example.com/iphone.jpg");

        when(service.updateProduct(eq(1L), any(ProductUpdateDto.class)))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/products/1")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("name", "iPhone 16 Pro")
                        .param("price", "85000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone 16 Pro"));

        verify(service).updateProduct(eq(1L), any(ProductUpdateDto.class));
    }

}