package com.shopsmart.retailer.controller;

import com.shopsmart.retailer.entity.Retailer;
import com.shopsmart.retailer.service.RetailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/shopsmart/retailer")
public class RetailerController {

    @Autowired
    private RetailerService retailerService;

    // 1. Get all retailers
    @GetMapping
    public List<Retailer> getAllRetailers() {
        return retailerService.getAllRetailers();
    }

    // 2. Get retailer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Retailer> getRetailerById(
            @PathVariable Integer id) {

        return retailerService.getRetailerById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .build()
                );
    }

    // 3. Create retailer
    @PostMapping
    public ResponseEntity<Retailer> createRetailer(
            @RequestBody Retailer retailer) {

        Retailer savedRetailer =
                retailerService.createRetailer(retailer);

        return new ResponseEntity<>(
                savedRetailer,
                HttpStatus.CREATED
        );
    }

    // 4. Update retailer
    @PutMapping("/{id}")
    public ResponseEntity<Retailer> updateRetailer(
            @PathVariable Integer id,
            @RequestBody Retailer retailerDetails) {

        try {
            Retailer updatedRetailer =
                    retailerService.updateRetailer(
                            id,
                            retailerDetails
                    );

            return ResponseEntity.ok(updatedRetailer);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    // 5. Delete retailer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetailer(
            @PathVariable Integer id) {

        try {
            retailerService.deleteRetailer(id);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }
}