package com.etnopolino.HotelServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HotelServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelServerApplication.class, args);
	}

}
