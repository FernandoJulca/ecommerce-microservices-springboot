package com.example.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.payment.model.Payment;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Integer>{

	Optional<Payment> findByOrderId(Integer orderId);
    List<Payment> findByUserEmail(String userEmail);
    boolean existsByOrderId(Integer orderId);
}
