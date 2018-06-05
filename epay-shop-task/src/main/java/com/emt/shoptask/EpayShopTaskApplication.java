package com.emt.shoptask;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.emt")
public class EpayShopTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpayShopTaskApplication.class, args);
	}
}
