package com.example.ManagerApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.ManagerApp.config.SchemaInit;

@SpringBootApplication
public class ManagerApp {

	public static void main(String[] args) {

		SchemaInit.init();

		SpringApplication.run(ManagerApp.class, args);
	}
}
