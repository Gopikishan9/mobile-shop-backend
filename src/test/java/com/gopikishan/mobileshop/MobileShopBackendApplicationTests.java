
package com.gopikishan.mobileshop;

import com.gopikishan.mobileshop.entity.Product;
import com.gopikishan.mobileshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MobileShopBackendApplicationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindProduct() {
        Product product = new Product();
        product.setName("Integration Test Phone");
        product.setBrand("TestBrand");
        product.setPrice((long)15000.0);
        product.setCategory("Smartphone");
        product.setDescription("Product for integration testing");

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());

        Product foundProduct = productRepository
                .findById(savedProduct.getId())
                .orElseThrow();

        assertEquals("Integration Test Phone", foundProduct.getName());
    }






}
