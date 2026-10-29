package com.gopikishan.mobileshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(
        name = "products"
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Brand is required")
    private String brand;
    @NotNull(message = "Price is required")
    private Long price;
    @NotNull(message = "Old Price is required")
    private Long oldPrice;

    //    @NotBlank(message = "Image is required")
    private String image;

    @NotNull(message = "Category cannot be blank!")
    private String category;

    private Boolean inStock = true;

    @NotBlank(message = "Description is required!")
    private String description;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> specifications;

}
