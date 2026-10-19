package com.example.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer", description = "Customer management APIs")

public class CustomerController {

    @Autowired
    CustomerService service;
    @Operation(summary = "Create customer")

    @PostMapping
    public ResponseEntity<String> savecustomer(
            @RequestBody Requestdto requestData) {

        return ResponseEntity.ok(
                service.savetocustomerdb(requestData)
        );
    }
    @Operation(summary = "Get customer by ID")

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getCustomer(
            @PathVariable String id) {

        return service.searchbyId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(summary = "Update customer")

    @PutMapping("/{id}")
    public ResponseEntity<Optional<CustomerEntity>> updatingCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerDto request) {

        Optional<CustomerEntity> result = service.updateCustomer(id, request);

        if (result.equals("Customer not found")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
    @Operation(summary = "Delete customer")

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletecustomer(
            @PathVariable String id) {

        String result = service.deleteCustomer(id);

        if (result.equals("Customer not found")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}