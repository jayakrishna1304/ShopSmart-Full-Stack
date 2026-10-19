package com.shopsmart.shop.controller;

import com.shopsmart.shop.entity.Shop;
import com.shopsmart.shop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopsmart/shop")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(
            @PathVariable Integer id
    ) {
        Shop shop = shopService.getShopById(id)
                .orElseThrow(() ->
                        new RuntimeException("Shop not found " + id)
                );

        return ResponseEntity.ok(shop);
    }

    @GetMapping("/retailer/{ownerId}")
    public List<Shop> getShopsByOwnerId(
            @PathVariable Integer ownerId
    ) {
        return shopService.getShopsByOwnerId(ownerId);
    }

    // Check shop id connected with which retailer
    @GetMapping("/{id}/connected-to-Retailer")
    public ResponseEntity<Map<String, Object>> getShopWithRetailer(
            @PathVariable Integer id
    ) {
        Map<String, Object> response =
                shopService.getShopWithRetailerDetails(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Shop> createShop(
            @RequestBody Shop shop
    ) {
        Shop savedShop = shopService.createShop(shop);

        return new ResponseEntity<>(
                savedShop,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(
            @PathVariable Integer id,
            @RequestBody Shop shopDetails
    ) {
        Shop updatedShop =
                shopService.updateShop(id, shopDetails);

        return ResponseEntity.ok(updatedShop);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(
            @PathVariable Integer id
    ) {
        try {
            shopService.deleteShop(id);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }
}