package edu.neu.csye6200;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Driver class for the Spring Boot application
 * @author Amit Singh Tomar, Qing Mi
 * 
 */
@SpringBootApplication
public class Driver {

	public static void main(String[] args) {
		System.out.println("============Spring Boot Application Start===================\n\n");
		
		SpringApplication.run(Driver.class, args);
		
		System.out.println("\n\n============Spring Boot Application Running===================");
	}

}

