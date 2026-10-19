package com.example.VoucherService.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(
        name = "vouchers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "voucher_code")
        }
)
@Data
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Integer voucherId;

    @Column(
            name = "voucher_code",
            nullable = false,
            unique = true,
            length = 50
    )
    private String voucherCode;

    @Column(
            name = "discount_percentage",
            nullable = false
    )
    private Double discountPercentage;

    @Column(
            name = "active",
            nullable = false
    )
    private Boolean active = true;

    @Column(name = "minimum_order_amount")
    private Double minimumOrderAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "shop_id", nullable = false)
    private Integer shopId;
}