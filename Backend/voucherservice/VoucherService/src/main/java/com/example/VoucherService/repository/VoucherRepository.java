package com.example.VoucherService.repository;

import com.example.VoucherService.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository
        extends JpaRepository<Voucher, Integer> {

    // Find voucher during checkout using voucher code
    Optional<Voucher> findByVoucherCodeIgnoreCase(
            String voucherCode
    );

    // Check duplicate voucher code while creating/updating
    boolean existsByVoucherCodeIgnoreCase(
            String voucherCode
    );

    List<Voucher> findByOwnerId(
            Integer ownerId
    );

    List<Voucher> findByShopId(
            Integer shopId
    );

    List<Voucher> findByOwnerIdAndShopId(
            Integer ownerId,
            Integer shopId
    );
}