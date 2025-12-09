package edu.neu.csye6200.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example REST Controller
 * Returns JSON responses for API endpoints
 *
 * @author Qing Mi
 */
@RestController
public class HelloController {

  @GetMapping("/")
  public ResponseEntity<Map<String, String>> hello() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Welcome to Spring Boot REST API!");
    response.put("status", "success");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/hello")
  public ResponseEntity<Map<String, String>> helloWorld() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Hello World from Spring Boot!");
    response.put("status", "success");
    return ResponseEntity.ok(response);
  }
}

