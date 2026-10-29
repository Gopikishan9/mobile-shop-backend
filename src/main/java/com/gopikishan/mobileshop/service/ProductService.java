package com.gopikishan.mobileshop.service;

import com.gopikishan.mobileshop.dto.ProductRequestDto;
import com.gopikishan.mobileshop.dto.ProductResponseDto;
import com.gopikishan.mobileshop.dto.ProductUpdateDto;
import com.gopikishan.mobileshop.entity.Product;
import com.gopikishan.mobileshop.exception.ProductNotFoundException;
import com.gopikishan.mobileshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ImageStorageService imageStorageService;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository repository, ImageStorageService imageStorageService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.imageStorageService = imageStorageService;
        this.objectMapper = objectMapper;
    }

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) throws IOException {
        Product product = mapToEntity(productRequestDto);

        MultipartFile image = productRequestDto.getImage();

        if (image == null || image.isEmpty()){
            throw new IllegalArgumentException("Product image is required!");
        }

        String imageUrl = imageStorageService.uploadImage(image);

        product.setImage(imageUrl);

        Product savedProduct = repository.save(product);

        ProductResponseDto productResponseDto = mapToResponseDto(savedProduct);
        return productResponseDto;
    }

    private Product mapToEntity(ProductRequestDto productRequestDto) {

        Product product = new Product();

        product.setName(productRequestDto.getName());
        product.setBrand(productRequestDto.getBrand());
        product.setPrice(productRequestDto.getPrice());
        product.setOldPrice(productRequestDto.getOldPrice());
        product.setCategory(productRequestDto.getCategory());
        product.setInStock(productRequestDto.getInStock());
        product.setDescription(productRequestDto.getDescription());

        String specsJson = productRequestDto.getSpecifications();

        if (specsJson != null && !specsJson.isBlank()){
            Map<String, Object> specifications =
                    objectMapper.readValue(
                            specsJson,
                            new TypeReference<Map<String, Object>>(){}
                    );
            product.setSpecifications(specifications);
        }

        return product;
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();

        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setBrand(product.getBrand());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setOldPrice(product.getOldPrice());
        productResponseDto.setImage(product.getImage());
        productResponseDto.setCategory(product.getCategory());
        productResponseDto.setInStock(product.getInStock());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setSpecifications(product.getSpecifications());

        return productResponseDto;
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found with Id: " + id));

        ProductResponseDto productResponseDto = mapToResponseDto(product);

        return productResponseDto;
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = repository.findAll();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : products) {
            productResponseDtos.add(mapToResponseDto(product));
        }

        return productResponseDtos;
    }

    public ProductResponseDto updateProduct(Long id, ProductUpdateDto dto) throws IOException {

        Product existingProduct = repository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product Not Found with Id: " + id));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            existingProduct.setName(dto.getName());
        }

        if (dto.getBrand() != null && !dto.getBrand().isBlank()) {
            existingProduct.setBrand(dto.getBrand());
        }

        if (dto.getPrice() != null) {
            existingProduct.setPrice(dto.getPrice());
        }

        if (dto.getOldPrice() != null) {
            existingProduct.setOldPrice(dto.getOldPrice());
        }

        if (dto.getCategory() != null && !dto.getCategory().isBlank()) {
            existingProduct.setCategory(dto.getCategory());
        }

        if (dto.getInStock() != null) {
            existingProduct.setInStock(dto.getInStock());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            existingProduct.setDescription(dto.getDescription());
        }

        String specsJson = dto.getSpecifications();

        if (specsJson != null && !specsJson.isBlank()) {
            Map<String, Object> specifications =
                    objectMapper.readValue(
                            specsJson,
                            new TypeReference<Map<String, Object>>() {}
                    );

            existingProduct.setSpecifications(specifications);
        }

        MultipartFile image = dto.getImage();

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageStorageService.uploadImage(image);
            existingProduct.setImage(imageUrl);
        }

        Product updatedProduct = repository.save(existingProduct);

        return mapToResponseDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found with Id: " + id));
        repository.delete(product);
    }

}
