package com.gopikishan.mobileshop.controller;

import com.gopikishan.mobileshop.dto.ProductRequestDto;
import com.gopikishan.mobileshop.dto.ProductResponseDto;
import com.gopikishan.mobileshop.dto.ProductUpdateDto;
import com.gopikishan.mobileshop.entity.Product;
import com.gopikishan.mobileshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Product APIs", description = "Product's CRUD")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "Add product")
    @PostMapping(
            value = "/products",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<ProductResponseDto> addProduct(@Valid @ModelAttribute ProductRequestDto productRequestDto) throws IOException {
        ProductResponseDto productResponseDto = service.addProduct(productRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
//        return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get product by id")
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id){
        ProductResponseDto productResponseDto = service.getProductById(id);
        return ResponseEntity.ok(productResponseDto);
    }

    @Operation(summary = "Get all products")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        List<ProductResponseDto> productResponseDtos = service.getAllProducts();
        return ResponseEntity.ok(productResponseDtos);
    }

    @Operation(summary = "Update product")
    @PutMapping(
            value = "/products/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @ModelAttribute ProductUpdateDto dto) throws IOException{
        ProductResponseDto productResponseDto = service.updateProduct(id, dto);
        return ResponseEntity.ok(productResponseDto);
    }

    @Operation(summary = "Delete product by id")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
