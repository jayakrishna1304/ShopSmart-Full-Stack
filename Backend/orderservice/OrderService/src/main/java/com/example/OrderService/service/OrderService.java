package com.example.OrderService.service;

import com.example.OrderService.client.*;
import com.example.OrderService.dto.*;
import com.example.OrderService.entity.OrderEntity;
import com.example.OrderService.entity.OrderItemEntity;
import com.example.OrderService.repository.OrderItemRepository;
import com.example.OrderService.repository.OrderRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private ProductClient productClient;

    @Getter
    @Autowired
    private LoyaltyClient loyaltyClient;

    @Setter
    @Getter
    @Autowired
    private LoyaltyTransactionClient loyaltyTransactionClient;
    @Getter
    @Setter
    @Autowired
    VoucherClient voucherClient;

    @Getter
    @Setter
    @Autowired
    OrderClient order;




    // =========================================================
// PRE-CHECKOUT CALCULATION
// =========================================================

    @Transactional
    public FareSummaryDto preCheckoutCalculate(
            Integer customerId,
            Integer pointsToRedeem,
            String voucherCode) {

        // ---------------------------------------------------------
        // GET CART
        // ---------------------------------------------------------

        List<CartResponseDto> cartItems =
                cartClient.getCartByCustomer(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException(
                    "Calculation failed. Shopping cart is empty."
            );
        }


        // ---------------------------------------------------------
        // CALCULATE SUBTOTAL
        // Product-level discount is already handled by
        // product.getFinalPrice()
        // ---------------------------------------------------------

        double subTotal = 0.0;

        for (CartResponseDto item : cartItems) {

            ProductDto product =
                    productClient.getProductDetails(
                            item.getProductId()
                    );

            if (product == null) {
                throw new RuntimeException(
                        "Product not found ID: "
                                + item.getProductId()
                );
            }

            subTotal +=
                    product.getFinalPrice()
                            * item.getQuantity();
        }


        // ---------------------------------------------------------
        // VOUCHER DISCOUNT
        // If voucher is NOT provided, discount remains 0.
        // ---------------------------------------------------------

        double voucherDiscount = 0.0;

        if (voucherCode != null
                && !voucherCode.trim().isEmpty()) {

            VoucherDto voucher =
                    voucherClient.getVoucherByCode(
                            voucherCode.trim()
                    );


            // -----------------------------------------------------
            // CHECK VOUCHER EXISTS
            // -----------------------------------------------------

            if (voucher == null) {
                throw new RuntimeException(
                        "Invalid voucher code."
                );
            }


            // -----------------------------------------------------
            // CHECK ACTIVE
            // -----------------------------------------------------

            if (voucher.getActive() == null
                    || !voucher.getActive()) {

                throw new RuntimeException(
                        "Voucher is not active."
                );
            }


            // -----------------------------------------------------
            // CHECK MINIMUM ORDER AMOUNT
            // -----------------------------------------------------

            if (voucher.getMinimumOrderAmount() != null
                    && subTotal < voucher.getMinimumOrderAmount()) {

                throw new RuntimeException(
                        "Minimum order amount for this voucher is ₹"
                                + voucher.getMinimumOrderAmount()
                );
            }


            // -----------------------------------------------------
            // CHECK START DATE
            // -----------------------------------------------------

            LocalDate today = LocalDate.now();

            if (voucher.getStartDate() != null
                    && today.isBefore(
                    voucher.getStartDate())) {

                throw new RuntimeException(
                        "Voucher is not active yet."
                );
            }


            // -----------------------------------------------------
            // CHECK END DATE
            // -----------------------------------------------------

            if (voucher.getEndDate() != null
                    && today.isAfter(
                    voucher.getEndDate())) {

                throw new RuntimeException(
                        "Voucher has expired."
                );
            }


            // -----------------------------------------------------
            // CHECK DISCOUNT PERCENTAGE
            // -----------------------------------------------------

            Double discountPercentage =
                    voucher.getDiscountPercentage();

            if (discountPercentage == null) {

                throw new RuntimeException(
                        "Voucher discount percentage is missing."
                );
            }

            if (discountPercentage <= 0) {

                throw new RuntimeException(
                        "Voucher discount must be greater than 0."
                );
            }

            if (discountPercentage > 100) {

                throw new RuntimeException(
                        "Voucher discount cannot exceed 100%."
                );
            }


            // -----------------------------------------------------
            // CALCULATE VOUCHER DISCOUNT
            // -----------------------------------------------------

            voucherDiscount =
                    subTotal
                            * discountPercentage
                            / 100.0;

            voucherDiscount =
                    Math.round(
                            voucherDiscount * 100.0
                    ) / 100.0;
        }


        // ---------------------------------------------------------
        // AMOUNT AFTER VOUCHER
        //
        // If no voucher:
        // voucherDiscount = 0
        // amountAfterVoucher = subTotal
        // ---------------------------------------------------------

        double amountAfterVoucher =
                subTotal - voucherDiscount;

        if (amountAfterVoucher < 0) {
            amountAfterVoucher = 0.0;
        }


        // ---------------------------------------------------------
        // GET LOYALTY POINTS TO REDEEM
        //
        // If no points are provided:
        // redeemed = 0
        // ---------------------------------------------------------

        int redeemed =
                (pointsToRedeem != null)
                        ? pointsToRedeem
                        : 0;


        // ---------------------------------------------------------
        // VALIDATE REDEEMED POINTS
        // ---------------------------------------------------------

        if (redeemed < 0) {

            throw new RuntimeException(
                    "Loyalty points to redeem cannot be negative."
            );
        }


        // ---------------------------------------------------------
        // LOYALTY POINTS CANNOT EXCEED PAYABLE AMOUNT
        // AFTER VOUCHER
        // ---------------------------------------------------------

        if (redeemed > amountAfterVoucher) {

            throw new RuntimeException(
                    "Loyalty points cannot exceed the amount payable after voucher discount."
            );
        }


        // ---------------------------------------------------------
        // CHECK AVAILABLE LOYALTY POINTS
        //
        // Only call Loyalty Service if customer actually
        // wants to redeem points.
        // ---------------------------------------------------------

        if (redeemed > 0) {

            Integer availablePoints =
                    loyaltyClient.availablepoints(
                            customerId
                    );

            if (availablePoints == null) {
                availablePoints = 0;
            }

            if (redeemed > availablePoints) {

                throw new RuntimeException(
                        "Insufficient loyalty points. Available points: "
                                + availablePoints
                );
            }
        }


        // ---------------------------------------------------------
        // CALCULATE FINAL PAYABLE AMOUNT
        //
        // Voucher is applied first.
        // Loyalty points are applied second.
        // ---------------------------------------------------------

        double finalPayable =
                amountAfterVoucher - redeemed;

        if (finalPayable < 0) {
            finalPayable = 0.0;
        }


        // ---------------------------------------------------------
        // CALCULATE LOYALTY POINTS EARNED
        // 1 point for every ₹100 spent
        // ---------------------------------------------------------

        int pointsEarned =
                (int) (finalPayable / 100);


        // ---------------------------------------------------------
        // RETURN FARE BREAKDOWN
        // ---------------------------------------------------------

        return new FareSummaryDto(
                Math.round(subTotal * 100.0) / 100.0,
                Math.round(voucherDiscount * 100.0) / 100.0,
                redeemed,
                Math.round(finalPayable * 100.0) / 100.0,
                pointsEarned
        );
    }

    // =========================================================
    // CONFIRM AND PLACE ORDER
    // =========================================================

    @Transactional
    public String confirmAndPlaceOrder(
            CheckoutRequestDto request) {

        // ---------------------------------------------------------
        // BASIC REQUEST VALIDATION
        // ---------------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Checkout request cannot be null."
            );
        }

        if (request.getCustomerId() == null) {

            throw new RuntimeException(
                    "Customer ID is required."
            );
        }

        if (request.getPaymentMethod() == null
                || request.getPaymentMethod().trim().isEmpty()) {

            throw new RuntimeException(
                    "Payment method is required."
            );
        }


        // ---------------------------------------------------------
        // UPI PAYMENT VALIDATION
        // ---------------------------------------------------------

        if ("UPI".equalsIgnoreCase(
                request.getPaymentMethod())) {

            String txnId =
                    request.getUpiTransactionId();

            if (txnId == null
                    || txnId.trim().isEmpty()
                    || txnId.trim().length() < 12) {

                throw new RuntimeException(
                        "Payment Verification Error: A valid 12-digit UPI Reference ID is required."
                );
            }
        }


        // ---------------------------------------------------------
        // GET CART
        // ---------------------------------------------------------

        List<CartResponseDto> cartItems =
                cartClient.getCartByCustomer(
                        request.getCustomerId()
                );

        if (cartItems == null
                || cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Checkout failed. Shopping cart is empty."
            );
        }


        // ---------------------------------------------------------
        // CALCULATE SUBTOTAL + VALIDATE STOCK
        // ALSO GET SHOP ID FROM PRODUCT SERVICE
        // ---------------------------------------------------------

        double checkoutSubTotal = 0.0;

        Integer actualShopId = null;

        for (CartResponseDto item : cartItems) {

            ProductDto product =
                    productClient.getProductDetails(
                            item.getProductId()
                    );

            System.out.println(product);

            if (product == null) {

                throw new RuntimeException(
                        "Product not found ID: "
                                + item.getProductId()
                );
            }


            // -----------------------------------------------------
            // GET SHOP ID FROM PRODUCT SERVICE
            // -----------------------------------------------------

            if (actualShopId == null) {

                actualShopId =
                        product.getShopId();
            }


            // -----------------------------------------------------
            // VALIDATE SHOP ID
            // -----------------------------------------------------

            if (product.getShopId() == null) {

                throw new RuntimeException(
                        "Shop ID not found for product ID: "
                                + item.getProductId()
                );
            }


            // -----------------------------------------------------
            // CHECK STOCK
            // -----------------------------------------------------

            if (product.getStockLeft() == null) {

                throw new RuntimeException(
                        "Stock information unavailable for product: "
                                + product.getProductName()
                );
            }

            if (product.getStockLeft()
                    < item.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock left for: "
                                + product.getProductName()
                );
            }


            // -----------------------------------------------------
            // CALCULATE PRODUCT PRICE
            // -----------------------------------------------------

            checkoutSubTotal +=
                    product.getFinalPrice()
                            * item.getQuantity();
        }


        // ---------------------------------------------------------
        // GET POINTS TO REDEEM
        // ---------------------------------------------------------

        int pointsToRedeem =
                (request.getPointsToRedeem() != null)
                        ? request.getPointsToRedeem()
                        : 0;


        // ---------------------------------------------------------
        // VALIDATE REDEEMED POINTS
        // ---------------------------------------------------------

        if (pointsToRedeem < 0) {

            throw new RuntimeException(
                    "Loyalty points to redeem cannot be negative."
            );
        }


        // 1 loyalty point = ₹1

        if (pointsToRedeem > checkoutSubTotal) {

            throw new RuntimeException(
                    "Loyalty points cannot exceed the order subtotal."
            );
        }


        // ---------------------------------------------------------
        // CHECK CUSTOMER'S AVAILABLE LOYALTY POINTS
        // ---------------------------------------------------------

        if (pointsToRedeem > 0) {

            Integer availablePoints =
                    loyaltyClient.availablepoints(
                            request.getCustomerId()
                    );

            if (availablePoints == null) {
                availablePoints = 0;
            }

            if (pointsToRedeem > availablePoints) {

                throw new RuntimeException(
                        "Insufficient loyalty points. Available points: "
                                + availablePoints
                );
            }
        }


        // ---------------------------------------------------------
        // VALIDATE SHOP ID
        // ---------------------------------------------------------

        if (actualShopId == null) {

            throw new RuntimeException(
                    "Shop ID could not be determined for this order."
            );
        }


        // ---------------------------------------------------------
        // CALCULATE FINAL PAYABLE AMOUNT
        // ---------------------------------------------------------

        double finalPayableAmount =
                checkoutSubTotal - pointsToRedeem;

        if (finalPayableAmount < 0) {
            finalPayableAmount = 0.0;
        }


        // ---------------------------------------------------------
        // CALCULATE EARNED LOYALTY POINTS
        // ---------------------------------------------------------

        int pointsEarned =
                (int) (finalPayableAmount / 100);


        // ---------------------------------------------------------
        // USE PRODUCT STOCK
        // ---------------------------------------------------------

        for (CartResponseDto item : cartItems) {

            productClient.useStock(
                    item.getProductId(),
                    item.getQuantity()
            );
        }


        // ---------------------------------------------------------
        // CREATE ORDER
        // ---------------------------------------------------------

        OrderEntity order =
                new OrderEntity();

        order.setCustomerId(
                request.getCustomerId()
        );


        // IMPORTANT:
        // Shop ID comes from Product Service,
        // NOT from frontend request.

        order.setShopId(
                actualShopId
        );


        order.setTotalAmount(
                Math.round(
                        finalPayableAmount * 100.0
                ) / 100.0
        );

        order.setPaymentMethod(
                request.getPaymentMethod()
        );


        // ---------------------------------------------------------
        // DELIVERY ADDRESS + UPI TRANSACTION ID
        // ---------------------------------------------------------

        order.setDeliveryAddress(
                request.getDeliveryAddress()
        );

        if ("UPI".equalsIgnoreCase(
                request.getPaymentMethod())) {

            order.setUpiTransactionId(
                    request.getUpiTransactionId()
            );

        } else {

            // COD orders do not have a UPI transaction ID.
            order.setUpiTransactionId(null);
        }


        // ---------------------------------------------------------
        // PAYMENT STATUS
        // ---------------------------------------------------------

        if ("UPI".equalsIgnoreCase(
                request.getPaymentMethod())) {

            order.setPaymentStatus("PAID");

        } else {

            order.setPaymentStatus("PENDING");
        }


        // ---------------------------------------------------------
        // LOYALTY INFORMATION IN ORDER
        // ---------------------------------------------------------

        order.setLoyaltyPointsEarned(
                pointsEarned
        );

        order.setLoyaltyPointsRedeemed(
                pointsToRedeem
        );


        // ---------------------------------------------------------
        // ORDER STATUS
        // ---------------------------------------------------------

        order.setStatus("PLACED");


        // ---------------------------------------------------------
        // SAVE ORDER
        // ---------------------------------------------------------

        OrderEntity savedOrder =
                orderRepository.save(order);


        // =========================================================
        // SAVE ORDER ITEMS
        // =========================================================

        for (CartResponseDto item : cartItems) {

            ProductDto product =
                    productClient.getProductDetails(
                            item.getProductId()
                    );

            OrderItemEntity orderItem =
                    new OrderItemEntity();

            orderItem.setOrderId(
                    savedOrder.getOrderId()
            );

            orderItem.setProductId(
                    item.getProductId()
            );

            orderItem.setQuantity(
                    item.getQuantity()
            );

            orderItem.setPriceAtPurchase(
                    product != null
                            ? product.getFinalPrice()
                            : 0.00
            );

            orderItemRepository.save(
                    orderItem
            );
        }


        // =========================================================
        // REDEEM LOYALTY POINTS
        // =========================================================

        if (pointsToRedeem > 0) {

            // Deduct points from Loyalty Service

            loyaltyClient.pointstoredeem(
                    request.getCustomerId(),
                    pointsToRedeem
            );


            // -----------------------------------------------------
            // Create REDEEMED transaction
            // -----------------------------------------------------

            LoyaltyTransactionRequestDto transaction =
                    new LoyaltyTransactionRequestDto();

            transaction.setCustomerId(
                    request.getCustomerId()
            );

            transaction.setOrderId(
                    savedOrder.getOrderId()
            );

            transaction.setTransactionType(
                    "REDEEMED"
            );

            transaction.setPoints(
                    pointsToRedeem
            );

            transaction.setDescription(
                    pointsToRedeem
                            + " loyalty points redeemed for Order #"
                            + savedOrder.getOrderId()
            );

            loyaltyTransactionClient.createTransaction(
                    transaction
            );
        }


        // =========================================================
        // ADD EARNED LOYALTY POINTS
        // =========================================================

        if (pointsEarned > 0) {

            // Add earned points to Loyalty Service

            loyaltyClient.addPoints(
                    request.getCustomerId(),
                    pointsEarned
            );


            // -----------------------------------------------------
            // Create EARNED transaction
            // -----------------------------------------------------

            LoyaltyTransactionRequestDto transaction =
                    new LoyaltyTransactionRequestDto();

            transaction.setCustomerId(
                    request.getCustomerId()
            );

            transaction.setOrderId(
                    savedOrder.getOrderId()
            );

            transaction.setTransactionType(
                    "EARNED"
            );

            transaction.setPoints(
                    pointsEarned
            );

            transaction.setDescription(
                    pointsEarned
                            + " loyalty points earned from Order #"
                            + savedOrder.getOrderId()
            );

            loyaltyTransactionClient.createTransaction(
                    transaction
            );
        }


        // =========================================================
        // CLEAR CART
        // =========================================================

        cartClient.clearCart(
                request.getCustomerId()
        );


        // =========================================================
        // RETURN SUCCESS
        // =========================================================

        return "Success|OrderID:"
                + savedOrder.getOrderId()
                + "|PaidAmount:"
                + order.getTotalAmount();
    }


    // =========================================================
    // GET CUSTOMER ORDERS
    // =========================================================

    public List<OrderEntity> getCustomerOrders(
            Integer customerId) {

        return orderRepository.findByCustomerId(
                customerId
        );
    }


    // =========================================================
    // GET ALL ORDERS FOR AI DUMP
    // =========================================================

    public List<OrderEntity> getAllOrdersForAiDump() {

        // Queries everything sitting inside your
        // MySQL transactional tables natively

        return orderRepository.findAll();
    }


    // =========================================================
    // GET SHOP ORDERS
    // =========================================================

    public List<OrderEntity> getShopOrders(
            Integer shopId) {

        return orderRepository.findByShopId(
                shopId
        );
    }


    public List<OrderEntity> getShopOrdersForRetailer(
            Integer shopId,
            Integer retailerId) {

        List<ShopDto> shops =
                order.getShopsByRetailer(retailerId);

        boolean ownsShop = shops.stream()
                .anyMatch(shop ->
                        shop.getShopId() != null
                                && shop.getShopId().equals(shopId)
                );

        if (!ownsShop) {
            throw new RuntimeException(
                    "You are not authorized to view orders for this shop."
            );
        }

        return getShopOrders(shopId);
    }
    // =========================================================
// UPDATE EXPECTED DELIVERY DATE
// =========================================================

    @Transactional
    public String updateExpectedDeliveryDate(
            Integer orderId,
            Integer retailerId,
            LocalDate expectedDeliveryDate) {

        if (orderId == null) {
            throw new RuntimeException(
                    "Order ID is required."
            );
        }

        if (retailerId == null) {
            throw new RuntimeException(
                    "Retailer ID is required."
            );
        }

        if (expectedDeliveryDate == null) {
            throw new RuntimeException(
                    "Expected delivery date is required."
            );
        }

        OrderEntity orderEntity =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: " + orderId
                                )
                        );

        // Check whether this retailer owns the shop
        // to which this order belongs.
        List<ShopDto> shops =
                order.getShopsByRetailer(retailerId);

        boolean ownsShop = shops.stream()
                .anyMatch(shop ->
                        shop.getShopId() != null
                                && shop.getShopId()
                                .equals(orderEntity.getShopId())
                );

        if (!ownsShop) {
            throw new RuntimeException(
                    "You are not authorized to update this order."
            );
        }

        orderEntity.setExpectedDeliveryDate(
                expectedDeliveryDate
        );

        orderRepository.save(orderEntity);

        return "Expected delivery date updated successfully for Order #"
                + orderId;
    }
    // =========================================================
// GET ORDER ITEMS
// =========================================================

    public List<OrderItemEntity> getOrderItems(
            Integer orderId) {

        return orderItemRepository.findByOrderId(orderId);
    }

}