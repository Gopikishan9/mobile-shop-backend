package com.gopikishan.mobileshop.service;

import com.gopikishan.mobileshop.dto.ProductRequestDto;
import com.gopikishan.mobileshop.dto.ProductResponseDto;
import com.gopikishan.mobileshop.dto.ProductUpdateDto;
import com.gopikishan.mobileshop.entity.Product;
import com.gopikishan.mobileshop.exception.ProductNotFoundException;
import com.gopikishan.mobileshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductService service;

    @Mock
    ProductRepository repository;
    @Mock
    ImageStorageService imageStorageService;
    @Mock
    ObjectMapper objectMapper;

    @Test
    void addProductShouldSaveProductAndReturnResponseDto() throws IOException {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("iPhone 16");
        request.setBrand("Apple");
        request.setPrice(75000L);
        request.setOldPrice(80000L);
        request.setCategory("Smartphone");
        request.setInStock(true);
        request.setDescription("Apple smartphone");

        Map<String, Object> specs = new HashMap<>();
        specs.put("RAM", "8GB");
        specs.put("Storage", "128GB");

        ObjectMapper realObjectMapper = new ObjectMapper();
        request.setSpecifications(realObjectMapper.writeValueAsString(specs));

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "iphone.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
        request.setImage(image);

        when(objectMapper.readValue(
                eq(request.getSpecifications()),
                any(tools.jackson.core.type.TypeReference.class)
        )).thenReturn(specs);

        when(imageStorageService.uploadImage(image))
                .thenReturn("https://example.com/iphone.jpg");

        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ProductResponseDto result = service.addProduct(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("iPhone 16", result.getName());
        assertEquals("Apple", result.getBrand());
        assertEquals(75000L, result.getPrice());
        assertEquals("https://example.com/iphone.jpg", result.getImage());
        assertEquals(specs, result.getSpecifications());

        verify(imageStorageService).uploadImage(image);
        verify(repository).save(any(Product.class));
    }

    @Test
    void addProductShouldThrowExceptionWhenImageIsMissing() throws IOException {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("iPhone 16");
        request.setBrand("Apple");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addProduct(request)
        );

        assertEquals("Product image is required!", exception.getMessage());

        verifyNoInteractions(imageStorageService);
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void addProductShouldThrowExceptionWhenImageIsEmpty() throws IOException {
        ProductRequestDto request = new ProductRequestDto();

        MockMultipartFile emptyImage = new MockMultipartFile(
                "image",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        request.setImage(emptyImage);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addProduct(request)
        );

        assertEquals("Product image is required!", exception.getMessage());

        verifyNoInteractions(imageStorageService);
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void updateProductShouldUpdateAndReturnResponseDto() throws IOException {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Phone");
        existing.setBrand("Apple");
        existing.setPrice(50000L);
        existing.setImage("old-image.jpg");
        existing.setCategory("Smartphone");
        existing.setInStock(true);
        existing.setDescription("Old description");

        ProductUpdateDto request = new ProductUpdateDto();
        request.setName("iPhone 16");
        request.setPrice(75000L);
        request.setDescription("Updated description");
        request.setSpecifications("{\"RAM\":\"8GB\",\"Storage\":\"128GB\"}");

        Map<String, Object> specs = new HashMap<>();
        specs.put("RAM", "8GB");
        specs.put("Storage", "128GB");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        when(objectMapper.readValue(
                eq(request.getSpecifications()),
                any(tools.jackson.core.type.TypeReference.class)
        )).thenReturn(specs);

        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto result = service.updateProduct(1L, request);

        assertEquals("iPhone 16", result.getName());
        assertEquals(75000L, result.getPrice());
        assertEquals("Updated description", result.getDescription());
        assertEquals(specs, result.getSpecifications());
        assertEquals("old-image.jpg", result.getImage());

        verify(repository).findById(1L);
        verify(repository).save(existing);
        verifyNoInteractions(imageStorageService);
    }

    @Test
    void updateProductShouldUploadNewImageWhenProvided() throws IOException {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("iPhone 16");
        existing.setImage("old-image.jpg");

        ProductUpdateDto request = new ProductUpdateDto();

        MockMultipartFile newImage = new MockMultipartFile(
                "image",
                "new-iphone.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );
        request.setImage(newImage);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(imageStorageService.uploadImage(newImage))
                .thenReturn("https://example.com/new-iphone.jpg");
        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto result = service.updateProduct(1L, request);

        assertEquals("https://example.com/new-iphone.jpg", result.getImage());

        verify(imageStorageService).uploadImage(newImage);
        verify(repository).save(existing);
    }

    @Test
    void updateProductShouldKeepOldImageWhenImageIsMissing() throws IOException {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("iPhone 16");
        existing.setImage("old-image.jpg");

        ProductUpdateDto request = new ProductUpdateDto();
        request.setName("iPhone 16 Pro");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto result = service.updateProduct(1L, request);

        assertEquals("iPhone 16 Pro", result.getName());
        assertEquals("old-image.jpg", result.getImage());

        verify(repository).save(existing);
        verifyNoInteractions(imageStorageService);
    }

    @Test
    void updateProductShouldUpdateSpecifications() throws IOException {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("iPhone 16");

        ProductUpdateDto request = new ProductUpdateDto();
        request.setSpecifications("{\"RAM\":\"12GB\",\"Storage\":\"256GB\"}");

        Map<String, Object> specs = new HashMap<>();
        specs.put("RAM", "12GB");
        specs.put("Storage", "256GB");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        when(objectMapper.readValue(
                eq(request.getSpecifications()),
                any(tools.jackson.core.type.TypeReference.class)
        )).thenReturn(specs);

        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto result = service.updateProduct(1L, request);

        assertEquals(specs, result.getSpecifications());

        verify(objectMapper).readValue(
                eq(request.getSpecifications()),
                any(tools.jackson.core.type.TypeReference.class)
        );
        verify(repository).save(existing);
    }

    @Test
    void deleteProductShouldDeleteExistingProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 16");

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.deleteProduct(1L);

        verify(repository).findById(1L);
        verify(repository).delete(product);
    }

    @Test
    void deleteProductShouldThrowExceptionWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.deleteProduct(99L)
        );

        verify(repository).findById(99L);
        verify(repository, never()).delete(any(Product.class));
    }

}