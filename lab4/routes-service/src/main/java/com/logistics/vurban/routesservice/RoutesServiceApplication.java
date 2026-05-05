package com.logistics.vurban.routesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RoutesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutesServiceApplication.class, args);
	}

}