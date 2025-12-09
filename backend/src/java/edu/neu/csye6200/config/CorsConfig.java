package edu.neu.csye6200.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for REST API.
 * Allows frontend applications to access the API.
 */
@Configuration
public class CorsConfig {

	@Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
	private String allowedOrigins;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		List<String> origins = Arrays.asList(allowedOrigins.split(","));
		config.setAllowedOrigins(origins);

		config.setAllowCredentials(true);
		config.setAllowedHeaders(Arrays.asList(
				"Authorization",
				"Content-Type",
				"Accept",
				"Origin",
				"X-Requested-With"));
		config.setAllowedMethods(Arrays.asList(
				"GET",
				"POST",
				"PUT",
				"DELETE",
				"OPTIONS"));
		config.setMaxAge(3600L);

		source.registerCorsConfiguration("/api/**", config);
		return new CorsFilter(source);
	}
}