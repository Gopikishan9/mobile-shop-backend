package com.gopikishan.mobileshop.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductUpdateDto {

    private String name;
    private String brand;
    private Long price;
    private Long oldPrice;
    private MultipartFile image;
    private String category;
    private Boolean inStock;
    private String description;
    private String specifications;
}