package com.example.OrderService.repository;

import com.example.OrderService.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByCustomerId(Integer customerId);
    List<OrderEntity> findByShopId(Integer shopId);
}
