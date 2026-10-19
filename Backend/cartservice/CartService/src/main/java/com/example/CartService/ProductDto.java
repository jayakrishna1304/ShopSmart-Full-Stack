package com.example.CartService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private Integer productId;

    private String productName;

    private String description;

    private Double originalPrice;

    private Double discount;

    private Integer stockLeft;

    private Integer stockUsed;

    private String category;

    private Integer shopId;

    private String imageUrl;


    public Double getFinalPrice() {

        if (originalPrice == null)
            return 0.00;

        double currentDiscount =
                (discount != null) ? discount : 0.00;

        double finalVal =
                originalPrice -
                        (originalPrice * currentDiscount / 100.0);

        return Math.round(finalVal * 100.0) / 100.0;
    }
}