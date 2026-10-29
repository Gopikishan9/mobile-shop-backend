package com.gopikishan.mobileshop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Long price;

    @PositiveOrZero(message = "Old price cannot be negative")
    private Long oldPrice;

    private MultipartFile image;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    private Boolean inStock = true;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description cannot exceed 100 characters!")
    private String description;

    private String specifications;
}