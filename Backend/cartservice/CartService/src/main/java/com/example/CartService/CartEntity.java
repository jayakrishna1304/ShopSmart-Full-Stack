package com.example.CartService;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_customer_product",
                columnNames = {"customer_id", "product_id"}
        )
})
@Getter
@Setter
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;


    // ============================================================
    // PRODUCT DETAILS
    // Matching Product entity field names
    // ============================================================

    @Column(name = "product_name")
    private String productName;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double originalPrice;

    @Column(name = "discount")
    private Double discount = 0.00;

    @Column(name = "stock_left")
    private Integer stockLeft = 0;

    @Column(name = "stock_used")
    private Integer stockUsed = 0;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;


    // ============================================================
    // CART TIMESTAMPS
    // ============================================================

    @Column(
            name = "created_at",
            updatable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updatedAt;
}