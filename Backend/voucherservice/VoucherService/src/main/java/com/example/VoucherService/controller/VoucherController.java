package com.example.VoucherService.controller;

import com.example.VoucherService.entity.Voucher;
import com.example.VoucherService.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voucher")
//@CrossOrigin(
//        origins = "http://localhost:5173",
//        allowedHeaders = "*",
//        methods = {
//                RequestMethod.GET,
//                RequestMethod.POST,
//                RequestMethod.PUT,
//                RequestMethod.PATCH,
//                RequestMethod.DELETE,
//                RequestMethod.OPTIONS
//        }
//)
@Tag(name = "Voucher Service")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    @Operation(summary = "Create a new voucher")
    public ResponseEntity<?> createVoucher(
            @RequestBody Voucher voucher) {

        try {

            Voucher created =
                    voucherService.createVoucher(voucher);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    @Operation(summary = "Get all vouchers")
    public ResponseEntity<List<Voucher>> getAllVouchers() {

        return ResponseEntity.ok(
                voucherService.getAllVouchers()
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{voucherId}")
    @Operation(summary = "Get voucher by ID")
    public ResponseEntity<?> getVoucherById(
            @PathVariable Integer voucherId) {

        try {

            return ResponseEntity.ok(
                    voucherService.getVoucherById(
                            voucherId
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // GET BY VOUCHER CODE
    // Used by Order Service
    // =========================================================

    @GetMapping("/code/{voucherCode}")
    @Operation(summary = "Get voucher by voucher code")
    public ResponseEntity<?> getVoucherByCode(
            @PathVariable String voucherCode) {

        try {

            return ResponseEntity.ok(
                    voucherService.getVoucherByCode(
                            voucherCode
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // GET BY OWNER
    // =========================================================

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get vouchers created by retailer")
    public ResponseEntity<List<Voucher>> getByOwner(
            @PathVariable Integer ownerId) {

        return ResponseEntity.ok(
                voucherService.getVouchersByOwner(
                        ownerId
                )
        );
    }


    // =========================================================
    // GET BY SHOP
    // =========================================================

    @GetMapping("/shop/{shopId}")
    @Operation(summary = "Get vouchers for a shop")
    public ResponseEntity<List<Voucher>> getByShop(
            @PathVariable Integer shopId) {

        return ResponseEntity.ok(
                voucherService.getVouchersByShop(
                        shopId
                )
        );
    }


    // =========================================================
    // GET BY OWNER + SHOP
    // =========================================================

    @GetMapping("/owner/{ownerId}/shop/{shopId}")
    @Operation(summary = "Get vouchers for retailer and shop")
    public ResponseEntity<List<Voucher>> getByOwnerAndShop(
            @PathVariable Integer ownerId,
            @PathVariable Integer shopId) {

        return ResponseEntity.ok(
                voucherService.getVouchersByOwnerAndShop(
                        ownerId,
                        shopId
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{voucherId}")
    @Operation(summary = "Update voucher")
    public ResponseEntity<?> updateVoucher(
            @PathVariable Integer voucherId,
            @RequestBody Voucher voucherDetails) {

        try {

            return ResponseEntity.ok(
                    voucherService.updateVoucher(
                            voucherId,
                            voucherDetails
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // ACTIVATE / DEACTIVATE
    // =========================================================

    @PatchMapping("/{voucherId}/status")
    @Operation(summary = "Activate or deactivate voucher")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer voucherId,
            @RequestParam Boolean active) {

        try {

            return ResponseEntity.ok(
                    voucherService.updateStatus(
                            voucherId,
                            active
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{voucherId}")
    @Operation(summary = "Delete voucher")
    public ResponseEntity<?> deleteVoucher(
            @PathVariable Integer voucherId) {

        try {

            voucherService.deleteVoucher(voucherId);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}