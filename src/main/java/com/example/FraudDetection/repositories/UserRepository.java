package com.example.FraudDetection.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FraudDetection.models.User;

public interface UserRepository extends JpaRepository<User,Long>{
	
	User findByEmail(String email);
	
	User findByEmailAndPassword(String email,String password);
	
	User findByPhoneNo(Long phoneNo);
}
