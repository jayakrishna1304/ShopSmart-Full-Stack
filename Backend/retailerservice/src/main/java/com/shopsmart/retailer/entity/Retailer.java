package com.shopsmart.retailer.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "retailer")
@Data
public class Retailer implements Persistable<Integer> { // 1. Implement Persistable

    @Id
    @Column(name = "owner_id") // No GenerationType.IDENTITY strategy here!
    private Integer ownerId;

    @Column(name = "owner_name", nullable = false, length = 50)
    private String ownerName;

    @Column(name = "owner_email", nullable = false, unique = true, length = 100)
    private String ownerEmail;

    @Column(name = "owner_password", nullable = false)
    private String ownerPassword;

    // 2. Return the assigned ID explicitly
    @Override
    public Integer getId() {
        return this.ownerId;
    }

    // 3. FORCE Hibernate to run an INSERT instead of a merge update!
    @Transient // Tells JPA not to create a column for this flag in MySQL
    @Override
    public boolean isNew() {
        return true;
    }
}
