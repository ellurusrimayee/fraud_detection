package com.example.FraudDetection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FraudDetection.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long>{
	
	Payment findByUserEmail(String userEmail);
	
	Payment findByTransactionId(Long transactionId);
}
