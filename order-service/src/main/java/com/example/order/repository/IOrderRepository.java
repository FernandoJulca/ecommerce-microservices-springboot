package com.example.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.order.model.Order;
import com.example.order.model.OrderStatus;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);

	List<Order> findByStatus(OrderStatus status);
}
