package com.gopikishan.mobileshop.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private String brand;
    private Long price;
    private Long oldPrice;
    private String image;
    private String category;
    private Boolean inStock;
    private String description;
    private Map<String, Object> specifications;
}