package com.example.AIServicenew.service;

import com.example.AIServicenew.client.CartDataClient;
import com.example.AIServicenew.client.OrderDataClient;
import com.example.AIServicenew.dto.AiPlatformOutputDto;
import com.example.AIServicenew.dto.ExternalDataDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiEngineService {

    @Autowired
    private CartDataClient cartDataClient;

    @Autowired
    private OrderDataClient orderDataClient;


    // ============================================================
    // GET CART DATA
    // ============================================================

    public List<ExternalDataDto> getCartData() {

        try {

            List<ExternalDataDto> cartData =
                    cartDataClient.getAllActiveCartSessionsDump();

            if (cartData == null) {
                return new ArrayList<>();
            }

            System.out.println(
                    "AI SERVICE - Cart records received: "
                            + cartData.size()
            );

            // IMPORTANT DEBUG
            System.out.println(
                    "AI SERVICE - CART DATA: "
                            + cartData
            );

            return cartData;

        } catch (Exception e) {

            System.err.println(
                    "CART SERVICE ERROR: "
                            + e.getClass().getName()
                            + " - "
                            + e.getMessage()
            );

            e.printStackTrace();

            return new ArrayList<>();
        }
    }


    // ============================================================
    // GET ORDER DATA
    // ============================================================

    public List<ExternalDataDto> getOrderData() {

        try {

            List<ExternalDataDto> orderData =
                    orderDataClient.getAllHistoricalPurchasesDump();

            if (orderData == null) {
                return new ArrayList<>();
            }

            System.out.println(
                    "AI SERVICE - Order records received: "
                            + orderData.size()
            );

            // IMPORTANT DEBUG
            System.out.println(
                    "AI SERVICE - ORDER DATA: "
                            + orderData
            );

            return orderData;

        } catch (Exception e) {

            System.err.println(
                    "ORDER SERVICE ERROR: "
                            + e.getClass().getName()
                            + " - "
                            + e.getMessage()
            );

            e.printStackTrace();

            return new ArrayList<>();
        }
    }


    // ============================================================
    // CALCULATE PLATFORM INSIGHTS
    // ============================================================

    public List<AiPlatformOutputDto> calculatePlatformInsights() {

        List<AiPlatformOutputDto> insights =
                new ArrayList<>();

        List<ExternalDataDto> cartDump =
                getCartData();

        List<ExternalDataDto> orderDump =
                getOrderData();


        // ========================================================
        // ABANDONED CART
        // ========================================================

        for (ExternalDataDto cart : cartDump) {

            if (cart == null) {
                continue;
            }

            if (cart.getCartUpdatedAt() == null) {
                continue;
            }

            long daysStale =
                    ChronoUnit.DAYS.between(
                            cart.getCartUpdatedAt(),
                            LocalDateTime.now()
                    );

            if (daysStale >= 7) {

                String productName =
                        cart.getProductName();

                if (productName == null) {

                    productName =
                            "Product ID: "
                                    + cart.getProductId();
                }

                insights.add(
                        new AiPlatformOutputDto(

                                cart.getCustomerId(),

                                "ABANDONED_CART",

                                productName,

                                "UNCHECKED_DAYS: "
                                        + daysStale,

                                "You left items in your cart "
                                        + "for over a week. "
                                        + "Use coupon LOCAL100 "
                                        + "to get ₹100 off."
                        )
                );
            }
        }


        // ========================================================
        // NO ORDERS
        // ========================================================

        if (orderDump.isEmpty()) {

            System.out.println(
                    "AI SERVICE - No order records received."
            );

            return insights;
        }


        // ========================================================
        // GROUP ORDERS BY CUSTOMER
        // ========================================================

        Map<Long, List<ExternalDataDto>>
                customerOrdersMap =

                orderDump.stream()

                        .filter(Objects::nonNull)

                        .filter(o ->
                                o.getCustomerId() != null)

                        .collect(
                                Collectors.groupingBy(
                                        ExternalDataDto::getCustomerId
                                )
                        );


        // ========================================================
        // CUSTOMER ANALYTICS
        // ========================================================

        for (
                Map.Entry<Long, List<ExternalDataDto>> entry
                : customerOrdersMap.entrySet()
        ) {

            Long customerId =
                    entry.getKey();

            List<ExternalDataDto>
                    userHistoryList =
                    entry.getValue();


            // TOTAL SPEND

            double totalSpend =
                    userHistoryList.stream()

                            .mapToDouble(
                                    order ->

                                            order.getOrderTotalAmount()
                                                    != null

                                                    ? order.getOrderTotalAmount()

                                                    : 0.0
                            )

                            .sum();


            // LAST ORDER

            LocalDateTime lastActive =
                    userHistoryList.stream()

                            .map(
                                    ExternalDataDto
                                            ::getOrderCreatedAt
                            )

                            .filter(
                                    Objects::nonNull
                            )

                            .max(
                                    LocalDateTime::compareTo
                            )

                            .orElse(null);


            // LOYALTY

            String tier = "BRONZE";

            if (totalSpend >= 5000) {

                tier = "GOLD";

            } else if (totalSpend >= 1500) {

                tier = "SILVER";
            }


            insights.add(
                    new AiPlatformOutputDto(

                            customerId,

                            "LOYALTY_TIER",

                            "Customer Segment",

                            tier,

                            "Total spend tracked: ₹"
                                    + totalSpend
                    )
            );


            // DORMANCY

            if (lastActive != null) {

                long elapsedDays =
                        ChronoUnit.DAYS.between(
                                lastActive,
                                LocalDateTime.now()
                        );

                if (elapsedDays >= 21) {

                    insights.add(
                            new AiPlatformOutputDto(

                                    customerId,

                                    "DORMANCY_NUDGE",

                                    "Dormant Profile",

                                    "INACTIVE_DAYS: "
                                            + elapsedDays,

                                    "You haven't shopped "
                                            + "in 3 weeks. "
                                            + "Use SHOPSMART10 "
                                            + "for 10% off."
                            )
                    );
                }
            }
        }


        // ========================================================
        // MARKET BASKET
        // ========================================================

        Map<Long, List<ExternalDataDto>>
                transactionLinesMap =

                orderDump.stream()

                        .filter(Objects::nonNull)

                        .filter(o ->
                                o.getOrderId() != null)

                        .filter(o ->
                                o.getProductName() != null)

                        .collect(
                                Collectors.groupingBy(
                                        ExternalDataDto::getOrderId
                                )
                        );


        Map<String, Integer>
                itemPairFrequencies =
                new HashMap<>();


        for (
                List<ExternalDataDto> orderLines
                : transactionLinesMap.values()
        ) {

            if (orderLines.size() < 2) {
                continue;
            }

            for (
                    int i = 0;
                    i < orderLines.size();
                    i++
            ) {

                for (
                        int j = i + 1;
                        j < orderLines.size();
                        j++
                ) {

                    String pA =
                            orderLines
                                    .get(i)
                                    .getProductName();

                    String pB =
                            orderLines
                                    .get(j)
                                    .getProductName();

                    if (pA == null || pB == null) {
                        continue;
                    }

                    String pairKey;

                    if (pA.compareTo(pB) < 0) {

                        pairKey =
                                pA + " + " + pB;

                    } else {

                        pairKey =
                                pB + " + " + pA;
                    }

                    itemPairFrequencies.put(

                            pairKey,

                            itemPairFrequencies
                                    .getOrDefault(
                                            pairKey,
                                            0
                                    )
                                    + 1
                    );
                }
            }
        }


        // TOP 3 BUNDLES

        itemPairFrequencies
                .entrySet()
                .stream()

                .sorted(
                        Map.Entry
                                .<String, Integer>
                                        comparingByValue()
                                .reversed()
                )

                .limit(3)

                .forEach(entry ->

                        insights.add(
                                new AiPlatformOutputDto(

                                        0L,

                                        "TRENDING_BUNDLE",

                                        entry.getKey(),

                                        "COUNT: "
                                                + entry.getValue(),

                                        "This combination is "
                                                + "trending based "
                                                + "on customer orders."
                                )
                        )
                );


        return insights;
    }
}