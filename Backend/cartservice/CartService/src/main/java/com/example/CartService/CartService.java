package com.example.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    CartRepository db;

    @Autowired
    CartquantityInterface quantity;

    @Transactional
    public ResponseEntity<String> addingtocart(RequestDto request) {
        System.out.println(request.getProductId());
        System.out.println("=================================");
        System.out.println("Product ID received: " + request.getProductId());
        System.out.println("Customer ID received: " + request.getCustomerId());
        System.out.println("Quantity received: " + request.getQuantity());
        System.out.println("=================================");

        ProductDto product =
                quantity.getproduct(request.getProductId());

        if (product == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Error: The requested product ID does not exist.");
        }


        Optional<CartEntity> existingItem =
                db.findByCustomerIdAndProductId(
                        request.getCustomerId(),
                        request.getProductId()
                );


        int availableStock =
                product.getStockLeft();

        int totalRequestedQuantity =
                request.getQuantity();

        CartEntity cart;


        // ==========================================
        // EXISTING CART ITEM
        // ==========================================

        if (existingItem.isPresent()) {

            cart = existingItem.get();

            totalRequestedQuantity +=
                    cart.getQuantity();

        }

        // ==========================================
        // NEW CART ITEM
        // ==========================================

        else {

            cart = new CartEntity();

            cart.setCustomerId(
                    request.getCustomerId()
            );

            cart.setProductId(
                    request.getProductId()
            );

            cart.setCreatedAt(
                    LocalDateTime.now()
            );
        }


        // ==========================================
        // STOCK VALIDATION
        // ==========================================

        if (totalRequestedQuantity > availableStock) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Error: Only "
                                    + availableStock
                                    + " units available in stock. You requested a total of "
                                    + totalRequestedQuantity
                    );
        }


        // ==========================================
        // CART QUANTITY
        // ==========================================

        cart.setQuantity(
                totalRequestedQuantity
        );


        // ==========================================
        // COPY PRODUCT DATA
        // ==========================================

        cart.setProductName(
                product.getProductName()
        );

        cart.setDescription(
                product.getDescription()
        );

        cart.setOriginalPrice(
                product.getOriginalPrice()
        );

        cart.setDiscount(
                product.getDiscount()
        );

        cart.setStockLeft(
                product.getStockLeft()
        );

        cart.setStockUsed(
                product.getStockUsed()
        );

        cart.setCategory(
                product.getCategory()
        );

        cart.setShopId(
                product.getShopId()
        );

        cart.setImageUrl(
                product.getImageUrl()
        );


        cart.setUpdatedAt(
                LocalDateTime.now()
        );


        db.save(cart);


        return ResponseEntity.ok(
                "Product successfully processed in cart!"
        );
    }

    @Transactional
    public void deleteproducts(ResponseDto request) {
        Optional<CartEntity> existingItem = db.findByCustomerIdAndProductId(
                request.getCustomerId(),
                request.getProductId()
        );

        if (existingItem.isPresent()) {
            CartEntity cart = existingItem.get();
            int newQuantity = cart.getQuantity() - request.getQuantity();

            if (newQuantity > 0) {
                cart.setQuantity(newQuantity);
                db.save(cart);
            } else {
                db.delete(cart);
            }
        }
    }

    public ResponseEntity<List<CartEntity>> findallproducts() {
        return ResponseEntity.ok(db.findAll());
    }

    // 🌟 ADDED METHOD 1: Fetch active cart items mapped explicitly to one customer ID
    public ResponseEntity<List<ResponseDto>> getCartByCustomer(Integer customerId) {
        List<CartEntity> entities = db.findByCustomerId(customerId);
        List<ResponseDto> dtoList = new ArrayList<>();

        for (CartEntity entity : entities) {
            ResponseDto dto = new ResponseDto();
            dto.setId(Integer.valueOf(entity.getId()));
            dto.setProductName(entity.getProductName());
            dto.setDescription(entity.getDescription());
            dto.setCategory(entity.getCategory());
            dto.setDiscount(entity.getDiscount());
            dto.setImageUrl(entity.getImageUrl());
            dto.setCustomerId(entity.getCustomerId());
            dto.setProductId(entity.getProductId());
            dto.setOriginalPrice(entity.getOriginalPrice());
            dto.setQuantity(entity.getQuantity());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());
            System.out.println(dto.getProductName());
            dtoList.add(dto);
        }

        return ResponseEntity.ok(dtoList);
    }

    @Transactional
    public void clearCart(Integer customerId) {
        db.deleteByCustomerId(customerId);
    }
}
