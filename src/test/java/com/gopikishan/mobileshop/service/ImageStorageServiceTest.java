
package com.gopikishan.mobileshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageStorageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }


    @Test
    void uploadImageShouldReturnSecureUrl() throws IOException {
        MultipartFile image = new MockMultipartFile(
                "image",
                "phone.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );

        String expectedUrl = "https://res.cloudinary.com/demo/phone.jpg";

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", expectedUrl);

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(uploadResult);

        String result = imageStorageService.uploadImage(image);

        assertEquals(expectedUrl, result);
    }


    @Test
    void uploadImageShouldThrowIOExceptionWhenSecureUrlIsMissing() throws IOException {
        MultipartFile image = new MockMultipartFile(
                "image",
                "phone.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(uploadResult);

        IOException exception = assertThrows(
                IOException.class,
                () -> imageStorageService.uploadImage(image)
        );

        assertEquals(
                "Image is not loaded from cloud storage",
                exception.getMessage()
        );
    }


    @Test
    void uploadImageShouldPropagateIOExceptionWhenUploadFails() throws IOException {
        MultipartFile image = new MockMultipartFile(
                "image",
                "phone.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenThrow(new IOException("Cloudinary upload failed"));

        IOException exception = assertThrows(
                IOException.class,
                () -> imageStorageService.uploadImage(image)
        );

        assertEquals("Cloudinary upload failed", exception.getMessage());
    }


    @Test
    void uploadImageShouldUseCorrectFolderAndResourceType() throws IOException {
        MultipartFile image = new MockMultipartFile(
                "image",
                "phone.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://example.com/phone.jpg");

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(uploadResult);

        imageStorageService.uploadImage(image);

        verify(uploader).upload(
                eq("test-image-content".getBytes()),
                argThat(options ->
                        "mobile-shop/products".equals(options.get("folder"))
                                && "image".equals(options.get("resource_type"))
                )
        );
    }


}
