package com.example.deliveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DeliveryserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryserviceApplication.class, args);
	}

}
