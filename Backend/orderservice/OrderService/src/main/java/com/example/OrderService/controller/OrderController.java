package com.example.OrderService.controller;

import com.example.OrderService.dto.CheckoutRequestDto;
import com.example.OrderService.dto.FareSummaryDto;
import com.example.OrderService.entity.OrderEntity;
import com.example.OrderService.entity.OrderItemEntity;
import com.example.OrderService.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/shopsmart/orders")
@Tag(name = "Order & Checkout API")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 🌟 ADDED ENDPOINT FOR THE STANDALONE AI MICROSERVICE RETRIEVAL HOOK
    // This feeds the historical data pool directly into your Groq RAG analytics loop
    @GetMapping("/history/all-dump")
    @Operation(summary = "Internal AI Link: Fetch all raw purchase logs for algorithmic pattern mining data clusters")
    public ResponseEntity<List<OrderEntity>> getAllHistoricalPurchasesDump() {
        // Leverages your existing internal search logic mapped to fetch records
        return ResponseEntity.ok(orderService.getAllOrdersForAiDump());
    }

    @GetMapping("/pre-checkout")
    @Operation(summary = "Step 1: Calculate total fare money to render payment screens and QR codes")
    public ResponseEntity<FareSummaryDto> getFareBreakdown(
            @RequestParam("customerId") Integer customerId,

            @RequestParam(
                    value = "pointsToRedeem",
                    required = false,
                    defaultValue = "0"
            )
            Integer pointsToRedeem,

            @RequestParam(
                    value = "voucherCode",
                    required = false
            )
            String voucherCode) {

        return ResponseEntity.ok(
                orderService.preCheckoutCalculate(
                        customerId,
                        pointsToRedeem,
                        voucherCode
                )
        );
    }

    @PostMapping("/confirm-payment")
    @Operation(summary = "Step 2: Submit transaction ID post-redirect to verify payment and save order records")
    public ResponseEntity<String> processPaymentConfirmation(@RequestBody CheckoutRequestDto requestDto) {
        try {
            String receiptMessage = orderService.confirmAndPlaceOrder(requestDto);
            return ResponseEntity.ok(receiptMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Order Placement Refused: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderEntity>> getCustomerHistory(@PathVariable Integer customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }

    @GetMapping("/shop/{shopId}/retailer/{retailerId}")
    public ResponseEntity<List<OrderEntity>> getShopHistory(
            @PathVariable Integer shopId,
            @PathVariable Integer retailerId) {

        return ResponseEntity.ok(
                orderService.getShopOrdersForRetailer(shopId, retailerId)
        );
    }
    @PutMapping("/{orderId}/delivery-date")
    @Operation(
            summary = "Retailer updates the expected delivery date for an order"
    )
    public ResponseEntity<String> updateExpectedDeliveryDate(
            @PathVariable Integer orderId,
            @RequestParam("retailerId") Integer retailerId,
            @RequestParam("expectedDeliveryDate") LocalDate expectedDeliveryDate) {

        try {

            String message =
                    orderService.updateExpectedDeliveryDate(
                            orderId,
                            retailerId,
                            expectedDeliveryDate
                    );

            return ResponseEntity.ok(message);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body("Delivery Date Update Failed: " + e.getMessage());
        }
    }
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemEntity>> getOrderItems(
            @PathVariable Integer orderId) {

        return ResponseEntity.ok(
                orderService.getOrderItems(orderId)
        );
    }
}
