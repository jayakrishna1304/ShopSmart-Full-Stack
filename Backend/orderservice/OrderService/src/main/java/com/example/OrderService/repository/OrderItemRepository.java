package com.example.OrderService.repository;

import com.example.OrderService.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {

    List<OrderItemEntity> findByOrderId(Integer orderId);
}