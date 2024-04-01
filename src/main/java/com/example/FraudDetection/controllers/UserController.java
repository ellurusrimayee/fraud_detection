package com.example.FraudDetection.controllers;

import java.util.Random;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.FraudDetection.models.User;
import com.example.FraudDetection.repositories.UserRepository;


@RestController
@CrossOrigin(origins = "https://localhost:4200", methods = {RequestMethod.DELETE,RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
public class UserController {
	
	@Autowired
	private UserRepository userrepository;
	
	@PostMapping("/useradd")
	public User postUser(@RequestBody User user) {
		Random rand = new Random();
		long accno =0;
		for (int i = 0; i < 8; i++)
		    {
		        long n = rand.nextInt(10) + 0;
		        if(n==0)
		        {
		        	n=1;
		        }
		        accno = accno * 10 + n;
		        System.out.println(accno);
		      }
		user.setAccountNo(accno);
		return userrepository.save(user);
	}
	
	@PutMapping("/addpassword/{phoneNo}")
	public User putInvoice(@PathVariable("phoneNo") Long phoneNo,@RequestBody User newuser) {
		User userDB = userrepository.findByPhoneNo(phoneNo);
		userDB.setPassword(newuser.getPassword());
		return userrepository.save(userDB);
	}
	
	
	@GetMapping("/getbyEmail/{email}")
	public User getUserByEmail(@PathVariable String email) {
		return userrepository.findByEmail(email);
	}
	
	@GetMapping("/getbyPhoneNo/{phoneNo}")
	public User getUserByPhoneNo(@PathVariable Long phoneNo) {
		return userrepository.findByPhoneNo(phoneNo);
	}

	@GetMapping("/login/{email}/{password}")
	public User getUserByEmailAndPassword(@PathVariable String email,@PathVariable String password) {
		return userrepository.findByEmailAndPassword(email,password);
	}
	
}
