package edu.neu.csye6200.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example REST Controller
 * 
 * @author Qing Mi
 * 
 */
@RestController
public class HelloController {

	@GetMapping("/")
	public String hello() {
		return "Welcome to Spring Boot!";
	}

	@GetMapping("/hello")
	public String helloWorld() {
		return "Hello World from Spring Boot!";
	}
}

