package com.sayye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // 시큐리티 비활성화
public class SayyeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SayyeApplication.class, args);
	}

}
