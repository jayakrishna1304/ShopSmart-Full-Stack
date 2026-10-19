package com.example.VoucherService.service;

import com.example.VoucherService.entity.Voucher;
import com.example.VoucherService.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;


    // =========================================================
    // CREATE VOUCHER
    // =========================================================

    public Voucher createVoucher(Voucher voucher) {

        validateVoucher(voucher);

        // Verify ownerId against the logged-in retailer JWT
        verifyOwner(voucher.getOwnerId());

        String code =
                voucher.getVoucherCode()
                        .trim()
                        .toUpperCase();

        if (voucherRepository
                .existsByVoucherCodeIgnoreCase(code)) {

            throw new RuntimeException(
                    "Voucher code already exists: " + code
            );
        }

        voucher.setVoucherCode(code);

        if (voucher.getActive() == null) {
            voucher.setActive(true);
        }

        return voucherRepository.save(voucher);
    }


    // =========================================================
    // GET VOUCHER BY ID
    // =========================================================

    public Voucher getVoucherById(Integer voucherId) {

        return voucherRepository.findById(voucherId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Voucher not found with ID: "
                                        + voucherId
                        )
                );
    }


    // =========================================================
    // GET VOUCHER BY CODE
    // Used by Order Service during checkout
    // =========================================================

    public Voucher getVoucherByCode(String voucherCode) {

        if (voucherCode == null
                || voucherCode.trim().isEmpty()) {

            throw new RuntimeException(
                    "Voucher code is required."
            );
        }

        return voucherRepository
                .findByVoucherCodeIgnoreCase(
                        voucherCode.trim()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Voucher not found: "
                                        + voucherCode
                        )
                );
    }


    // =========================================================
    // GET ALL VOUCHERS
    // =========================================================

    public List<Voucher> getAllVouchers() {

        return voucherRepository.findAll();
    }


    // =========================================================
    // GET VOUCHERS BY OWNER
    // =========================================================

    public List<Voucher> getVouchersByOwner(
            Integer ownerId) {

        return voucherRepository.findByOwnerId(ownerId);
    }


    // =========================================================
    // GET VOUCHERS BY SHOP
    // =========================================================

    public List<Voucher> getVouchersByShop(
            Integer shopId) {

        return voucherRepository.findByShopId(shopId);
    }


    // =========================================================
    // GET VOUCHERS BY OWNER + SHOP
    // =========================================================

    public List<Voucher> getVouchersByOwnerAndShop(
            Integer ownerId,
            Integer shopId) {

        return voucherRepository
                .findByOwnerIdAndShopId(
                        ownerId,
                        shopId
                );
    }


    // =========================================================
    // UPDATE VOUCHER
    // =========================================================

    public Voucher updateVoucher(
            Integer voucherId,
            Voucher voucherDetails) {

        Voucher existingVoucher =
                getVoucherById(voucherId);

        // Verify that logged-in retailer owns
        // the existing voucher.
        verifyOwner(existingVoucher.getOwnerId());

        validateVoucher(voucherDetails);

        // Owner cannot be changed.
        if (!existingVoucher.getOwnerId()
                .equals(voucherDetails.getOwnerId())) {

            throw new RuntimeException(
                    "Voucher owner cannot be changed."
            );
        }

        // Verify supplied ownerId against JWT.
        verifyOwner(voucherDetails.getOwnerId());

        String newCode =
                voucherDetails.getVoucherCode()
                        .trim()
                        .toUpperCase();

        if (!existingVoucher
                .getVoucherCode()
                .equalsIgnoreCase(newCode)) {

            if (voucherRepository
                    .existsByVoucherCodeIgnoreCase(newCode)) {

                throw new RuntimeException(
                        "Voucher code already exists: "
                                + newCode
                );
            }
        }

        existingVoucher.setVoucherCode(newCode);

        existingVoucher.setDiscountPercentage(
                voucherDetails.getDiscountPercentage()
        );

        existingVoucher.setActive(
                voucherDetails.getActive()
        );

        existingVoucher.setMinimumOrderAmount(
                voucherDetails.getMinimumOrderAmount()
        );

        existingVoucher.setStartDate(
                voucherDetails.getStartDate()
        );

        existingVoucher.setEndDate(
                voucherDetails.getEndDate()
        );

        // Owner is intentionally not changed.
        existingVoucher.setShopId(
                voucherDetails.getShopId()
        );

        return voucherRepository.save(
                existingVoucher
        );
    }


    // =========================================================
    // ACTIVATE / DEACTIVATE
    // =========================================================

    public Voucher updateStatus(
            Integer voucherId,
            Boolean active) {

        Voucher voucher =
                getVoucherById(voucherId);

        verifyOwner(voucher.getOwnerId());

        if (active == null) {

            throw new RuntimeException(
                    "Active status is required."
            );
        }

        voucher.setActive(active);

        return voucherRepository.save(voucher);
    }


    // =========================================================
    // DELETE VOUCHER
    // =========================================================

    public void deleteVoucher(Integer voucherId) {

        Voucher voucher =
                getVoucherById(voucherId);

        verifyOwner(voucher.getOwnerId());

        voucherRepository.deleteById(voucherId);
    }


    // =========================================================
    // GET LOGGED-IN USER ID FROM JWT
    // =========================================================

    private Integer getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {

            throw new RuntimeException(
                    "Authentication is required."
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {

            throw new RuntimeException(
                    "Invalid JWT authentication."
            );
        }

        /*
         * JWT numeric claims may internally be represented
         * by different Number implementations.
         *
         * We immediately convert the value to Integer so
         * the rest of ShopSmart uses Integer IDs only.
         */
        Number userIdNumber =
                jwt.getClaim("userId");

        if (userIdNumber == null) {

            throw new RuntimeException(
                    "User ID is missing from JWT."
            );
        }

        Integer userId =
                userIdNumber.intValue();

        if (userId <= 0) {

            throw new RuntimeException(
                    "Invalid user ID in JWT."
            );
        }

        return userId;
    }


    // =========================================================
    // VERIFY OWNER
    // JWT userId must match voucher ownerId
    // =========================================================

    private void verifyOwner(Integer ownerId) {

        if (ownerId == null
                || ownerId <= 0) {

            throw new RuntimeException(
                    "Owner ID is required."
            );
        }

        Integer jwtUserId =
                getLoggedInUserId();

        if (!jwtUserId.equals(ownerId)) {

            throw new RuntimeException(
                    "You are not authorized to manage vouchers for this owner."
            );
        }
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateVoucher(
            Voucher voucher) {

        if (voucher == null) {

            throw new RuntimeException(
                    "Voucher details are required."
            );
        }

        if (voucher.getVoucherCode() == null
                || voucher.getVoucherCode()
                .trim()
                .isEmpty()) {

            throw new RuntimeException(
                    "Voucher code is required."
            );
        }

        if (voucher.getDiscountPercentage() == null) {

            throw new RuntimeException(
                    "Discount percentage is required."
            );
        }

        if (voucher.getDiscountPercentage() <= 0
                || voucher.getDiscountPercentage() > 100) {

            throw new RuntimeException(
                    "Discount percentage must be greater than 0 and cannot exceed 100."
            );
        }

        if (voucher.getMinimumOrderAmount() != null
                && voucher.getMinimumOrderAmount() < 0) {

            throw new RuntimeException(
                    "Minimum order amount cannot be negative."
            );
        }

        if (voucher.getStartDate() != null
                && voucher.getEndDate() != null
                && voucher.getEndDate()
                .isBefore(voucher.getStartDate())) {

            throw new RuntimeException(
                    "End date cannot be before start date."
            );
        }

        if (voucher.getOwnerId() == null
                || voucher.getOwnerId() <= 0) {

            throw new RuntimeException(
                    "Owner ID is required."
            );
        }

        if (voucher.getShopId() == null
                || voucher.getShopId() <= 0) {

            throw new RuntimeException(
                    "Shop ID is required."
            );
        }
    }
}