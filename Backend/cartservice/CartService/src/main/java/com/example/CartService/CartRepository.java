package com.example.CartService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    List<CartEntity> findByCustomerId(Integer customerId);

    Optional<CartEntity> findByCustomerIdAndProductId(Integer customerId, Integer productId);

    @Modifying
    @Transactional
    void deleteByCustomerId(Integer customerId);
}
