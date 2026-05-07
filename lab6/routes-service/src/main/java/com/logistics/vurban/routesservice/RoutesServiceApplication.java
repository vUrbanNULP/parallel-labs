package com.logistics.vurban.routesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@EnableJpaRepositories
public class RoutesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutesServiceApplication.class, args);
	}

}