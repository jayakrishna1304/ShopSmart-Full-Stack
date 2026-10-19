package com.shopsmart.product.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "description", length = 255)
    private String description;

    /*
     * Database column:
     * products.price
     *
     * This represents the original/base price.
     */
    @Column(name = "price", nullable = false)
    private Double originalPrice;

    @Column(name = "discount")
    private Double discount = 0.00;

    @Column(name = "stock_left")
    private Integer stockLeft = 0;

    @Column(name = "stock_used")
    private Integer stockUsed = 0;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "shop_id", nullable = false)
    private Integer shopId;

    @Column(
            name = "created_at",
            updatable = false
    )
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(
            name = "image_url",
            columnDefinition = "LONGTEXT"
    )
    private String imageUrl;


    // ============================================================
    // FRONTEND PRICE SUPPORT
    // ============================================================

    /*
     * Frontend sends:
     *
     * "price": 50000
     *
     * We store that value in the database's `price` column,
     * which is represented by originalPrice in this entity.
     */
    @JsonSetter("price")
    public void setPriceFromRequest(Double price) {
        this.originalPrice = price;
    }


    /*
     * Frontend can also send:
     *
     * "originalPrice": 55000
     *
     * If provided, this remains the original/base price.
     */
    @JsonProperty("originalPrice")
    public Double getOriginalPriceValue() {
        return originalPrice;
    }

    @JsonProperty("originalPrice")
    public void setOriginalPriceValue(Double originalPrice) {
        this.originalPrice = originalPrice;
    }


    // ============================================================
    // COMPUTED FINAL PRICE
    // ============================================================

    /*
     * `price` returned to frontend is the discounted/final price.
     *
     * Example:
     *
     * originalPrice = 1000
     * discount = 10
     *
     * price = 900
     */
    @JsonProperty("price")
    @Transient
    public Double getPrice() {

        if (originalPrice == null) {
            return 0.00;
        }

        double currentDiscount =
                discount != null ? discount : 0.00;

        double finalPrice =
                originalPrice -
                        (originalPrice * currentDiscount / 100.0);

        return Math.round(finalPrice * 100.0) / 100.0;
    }


    // ============================================================
    // TIMESTAMP
    // ============================================================

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }


    @PreUpdate
    public void setLastUpdate() {
        updatedAt = LocalDateTime.now();
    }
}