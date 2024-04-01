package com.example.FraudDetection.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.FraudDetection.models.Payment;
import com.example.FraudDetection.repositories.PaymentRepository;


import jakarta.mail.internet.MimeMessage;


@RestController
@CrossOrigin(origins = "https://localhost:4200", methods = {RequestMethod.DELETE,RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
public class PaymentController {
	
	@Autowired
	private PaymentRepository paymentrepository;
	@Autowired
	private JavaMailSender mailSender;

	long transId=0;
	String customerEmail=null;
	String customerName="";
	String result=null;
	long mfaotp=0;
	int statusflag=0;

	@GetMapping("frauddetect")
	public String method(Payment payment) throws IOException {
	    // DataRobot API endpoint URL
	    URL url = new URL("https://hexaware-technologies-limited-partner.dynamic.orm.datarobot.com/predApi/v1.0/deployments/65fc219434934626bfa99ee7/predictions");

	    // Open HTTP connection
	    HttpURLConnection http = (HttpURLConnection) url.openConnection();
	    http.setRequestMethod("POST");
	    http.setDoOutput(true);
	    http.setRequestProperty("Accept", "application/json");
	    http.setRequestProperty("DataRobot-Key", "d25b8e23-288e-a709-fdf4-66e1961dca19");
	    http.setRequestProperty("Authorization", "Bearer {\"NjNjZTVlZGM5MzVlNjg3ZDQ1M2Q4YjI5Om8rbWczL1hiWUR4VGpiTXA3MlQ2emlFM2hYaEZ2Q25LN2lERVBTRHNmekk9\"}");
	    http.setRequestProperty("Content-Type", "application/json");
	    System.out.println(payment.getTXN_Amount());
	    // Construct the request payload without average transaction amount
	    String data = "[{" +
	            "\"MCC\": " + payment.getMCC() + "," +
	            "\n\"TXN_Amount\": " + payment.getTXN_Amount() + "," +
	            "\n\"POS_Entry_Code\": " + payment.getPOS_Entry_Code() + "," +
	            "\n\"FWES_Code\": \"" + payment.getFWES_Code() + "\"," +
	            "\n\"FALCONE_Score\": \"" + payment.getFALCONE_Score() + "\"," +
	            "\n\"Currency_Code\": \"" + payment.getCurrency_Code() + "\"," +
	            "\n\"TXN_Level\": \"" + payment.getTXN_Level() + "\"" +
	            "}]";

	    // Convert data to bytes
	    byte[] out = data.getBytes(StandardCharsets.UTF_8);

	    // Write data to the output stream
	    OutputStream stream = http.getOutputStream();
	    stream.write(out);

	    // Read response
	    BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
	    StringBuilder sb = new StringBuilder();
	    String output;
	    while ((output = br.readLine()) != null) {
	        sb.append(output);
	    }
	    String response = sb.toString();
	    System.out.println("Response from DataRobot API: " + response);

	    // Extract prediction from the response
	    int k = response.indexOf("prediction\":");
	    System.out.println(k);
	    String prediction = response.substring(k + 13, k + 20);
	    System.out.println("Prediction: " + prediction);

	    // Close connection
	    http.disconnect();
	    return prediction;
	}

	   @PostMapping("/paymentadd")
	    public Payment postUser(@RequestBody Payment payment) throws IOException {
	        
	        Random rand = new Random();
	        long refno = 0;
	        for (int i = 0; i < 6; i++) {
	            long n = rand.nextInt(10) + 0;
	            if(n == 0) {
	                n = 1;
	            }
	            refno = refno * 10 + n;
	        }
	        if(payment.getTXN_Amount()<100) {
	        	payment.setTXN_Level("Low");
	        }else {
	        	payment.setTXN_Level("High");
	        }
	        transId = refno;;
	        System.out.println("generated--------------------------------" + refno);
	        System.out.println("copied-----------------------------------" + transId);
	        payment.setTransactionId(refno);
	        String prediction =method(payment);
	        if (prediction.equals("Approve")) {
	            // If prediction is approve, set payment status to successful
	            payment.setPaymentStatus("Successful");
	        } else if (prediction.equals("Decline")) {
	            // If prediction is decline, send OTP to user email
	            String otpStatus = sendOtpToEmail(payment.getUserEmail());
	            if (otpStatus.equals("sent")) {
	            	Scanner sc=new Scanner(System.in);
	            	long enteredotp=sc.nextLong();
	                String otpVerify=otpverification(enteredotp);
	                if(otpVerify.equals("correct")) {
	                	payment.setPaymentStatus("Success");
	                }
	                else {
	                payment.setPaymentStatus("Failure");
	                }
	            } else {
	                // If OTP sending fails, set payment status to failure
	                payment.setPaymentStatus("Failure");
	            }
	        }
	        
	        // Save payment details to the database
	        Long beneficiary = payment.getToAccount();
	        paymentrepository.save(payment);
	        
	        // Return the payment object
	        return payment;
	    }
		
	   
		
		@GetMapping("/getbytransactionId/{transactionId}")
		public Payment getPaymentByTransId(@PathVariable Long transactionId) {
			
			return paymentrepository.findByTransactionId(transactionId);
		}
		
		@GetMapping("/findemail/{userEmail}")
		public Payment getPaymentByEmail(@PathVariable String userEmail) {
			 return paymentrepository.findByUserEmail(userEmail);
		}
		
		

	   @GetMapping("/sendotp/{userEmail}")
		public String sendOtpToEmail(@PathVariable String userEmail) {
			customerEmail=userEmail;
			Random rand = new Random();
			long otp =0;
			int flag=0;
			for (int i = 0; i < 6; i++)
			    {
			        long n = rand.nextInt(10) + 0;
			        if(n==0)
			        {
			        	n=1;
			        }
			        otp = otp * 10 + n;
			        }
			mfaotp=otp;
			System.out.println("otp copied ----------------------"+mfaotp);
			try {
				System.out.println("Reached here");
				SSL();
				MimeMessage mimeMessage = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				//helper.setFrom("paymentfrauddetect@gmail.com");
				helper.setFrom("harvis.hexawarebanking@gmail.com");
				helper.setTo(userEmail);
			
				helper.setSubject("OTP VERIFICATION");
				helper.setText( "<html>"+ "<body>"
				+ "<div>Hi,</div><br>"
				+"<div>Kindly use this otp for verification.</div><br><br><br>"
				+"<div>OTP        : "+otp+"</div><br>"
				+"<div>Thanks & Regards,</div><br>"
				+"<div>Hexaware Banking</div>"
				
				+ "<img src='cid:leftSideImage' style='float:left;width:20%;height:20%;'/>" +  "</body>"
			            + "</html>", true);
			     
			     helper.addInline("leftSideImage", new File("banking_logo.png"));
			     mailSender.send(mimeMessage);

				System.out.println("Email sending complete.");
				flag=1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(flag==1) {
			return "sent"; }
			else {
				return "fail";
			}
		}
	   @GetMapping("/otpverify/{otp}")
		public String otpverification(@PathVariable Long otp) {
		   Payment payment = new Payment();
			if(otp!=mfaotp) {
				payment.setPaymentStatus("Failed");
				System.out.println("typed "+otp);
				System.out.println("mfaotp "+mfaotp);
				try {
					System.out.println("1 ");
					SSL();
					MimeMessage mimeMessage = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
					helper.setFrom("paymentfrauddetect@gmail.com");
				//	helper.setFrom("harvis.hexawarebanking@gmail.com");
					helper.setTo(customerEmail);
					//helper.setTo("gayathrikanagaraj28@gmail.com");
					helper.setSubject("Incorrect OTP Alert ");
					System.out.println("2 ");
					helper.setText( "<html>"+ "<body>"
					+ "<div>Hi,</div><br>"
					+"<div>This is to inform you that your transaction has recalled due to incorrect OTP.</div><br><br><br>"
					+"<div>Please contact the bank for any clarifications</div><br><br><br>"
					+"<div>Thanks & Regards,</div><br>"
					+"<div>Hexaware Banking</div>"
					
					+ "<img src='cid:leftSideImage' style='float:left;width:20%;height:20%;'/>" +  "</body>"
				            + "</html>", true);
				     
				     helper.addInline("leftSideImage", new File("banking_logo.png"));
				     System.out.println("3 ");
				     mailSender.send(mimeMessage);

					System.out.println("Email sending complete.");
					
				} catch (Exception e) {
					
					System.out.println(e);
					e.printStackTrace();
				}
				return "wrong";
			}
			else {
				payment.setPaymentStatus("Successful");
				return "correct";
			}
			
		}
	   public void SSL() {
			SSLContext ctx;
			try {
			ctx = SSLContext.getInstance("TLS");

			ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
			SSLContext.setDefault(ctx);
			}
			catch( KeyManagementException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		
}
