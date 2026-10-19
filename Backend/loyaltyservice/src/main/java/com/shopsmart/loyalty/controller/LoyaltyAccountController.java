package com.shopsmart.loyalty.controller;

import com.shopsmart.loyalty.entity.LoyaltyAccount;
import com.shopsmart.loyalty.service.LoyaltyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopsmart/loyalty")
@CrossOrigin(origins = "http://localhost:5173")
public class LoyaltyAccountController {

    @Autowired
    private LoyaltyAccountService service;

    @GetMapping
    public List<LoyaltyAccount> getAllAccounts() {
        return service.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoyaltyAccount> getAccountById(
            @PathVariable Integer id) {

        LoyaltyAccount account = service.getAccountById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Account not found: " + id
                        ));

        return ResponseEntity.ok(account);
    }


    /*
     * CUSTOMER ID BASED
     *
     * If account exists -> return it.
     * If account doesn't exist -> create it with 0 points.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<LoyaltyAccount> getAccountByCustomerId(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                service.getOrCreateAccount(customerId)
        );
    }


    @PostMapping
    public ResponseEntity<LoyaltyAccount> createAccount(
            @RequestBody LoyaltyAccount account) {

        return new ResponseEntity<>(
                service.createAccount(account),
                HttpStatus.CREATED
        );
    }


    /*
     * OLD LOYALTY-ID BASED ENDPOINT
     *
     * Keep it if you want, but DO NOT use this
     * from Order Service with customerId.
     */
    @PatchMapping("/{id}/add-points")
    public ResponseEntity<LoyaltyAccount> addPoints(
            @PathVariable Integer id,
            @RequestParam Integer points) {

        return ResponseEntity.ok(
                service.updatePoints(id, points)
        );
    }


    @PatchMapping("/{id}/redeem-points")
    public ResponseEntity<LoyaltyAccount> redeemPoints(
            @PathVariable Integer id,
            @RequestParam Integer points) {

        return ResponseEntity.ok(
                service.redeemPoints(id, points)
        );
    }


    @GetMapping("/{id}/history")
    public ResponseEntity<String> getPointsHistory(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                service.getPointsHistory(id)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Integer id) {

        service.deleteAccount(id);

        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(
            RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }


    @GetMapping("/{id}/points-avail")
    public ResponseEntity<Integer> getPointsAvail(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                service.getPointsAvail(id)
        );
    }


    @GetMapping("/{id}/points-redeemed")
    public ResponseEntity<Integer> getPointsRedeemed(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                service.getPointsRedeemed(id)
        );
    }


    /*
     * THIS IS THE ENDPOINT ORDER SERVICE SHOULD USE
     *
     * customerId comes from the authenticated customer.
     *
     * If account doesn't exist:
     *     create account
     *     points = 0
     *
     * Then add the requested points.
     */
    @PatchMapping("/customer/{customerId}/add-points")
    public ResponseEntity<LoyaltyAccount> addPointsByCustomer(
            @PathVariable Integer customerId,
            @RequestParam Integer points) {

        return ResponseEntity.ok(
                service.updatePointsByCustomer(
                        customerId,
                        points
                )
        );
    }


    /*
     * THIS IS THE ENDPOINT ORDER SERVICE SHOULD USE
     * FOR REDEMPTION.
     */
    @PatchMapping("/customer/{customerId}/redeem-points")
    public ResponseEntity<LoyaltyAccount> redeemPointsByCustomer(
            @PathVariable Integer customerId,
            @RequestParam Integer points) {

        return ResponseEntity.ok(
                service.redeemPointsByCustomer(
                        customerId,
                        points
                )
        );
    }


    /*
     * Get available points using customerId.
     *
     * If account doesn't exist,
     * automatically create it with 0.
     */
    @GetMapping("/customer/{customerId}/points-avail")
    public ResponseEntity<Integer> getPointsAvailByCustomer(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                service.getPointsAvailByCustomer(customerId)
        );
    }
}