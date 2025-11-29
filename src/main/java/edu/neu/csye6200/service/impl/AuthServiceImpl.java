package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.AuthDTO;
import edu.neu.csye6200.dto.request.AuthRequest;
import edu.neu.csye6200.model.domain.User;
import edu.neu.csye6200.repository.UserRepository;
import edu.neu.csye6200.service.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.neu.csye6200.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO authenticate(AuthRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if user is enabled
        if (!user.getEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token (email is used as subject)
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        // Build response
        AuthDTO response = new AuthDTO();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setEmail(user.getEmail());
        response.setUserId(user.getId());
        response.setBusinessPersonId(
                user.getBusinessPerson() != null ? user.getBusinessPerson().getId() : null
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO refreshToken(String token) {
        // Validate current token
        if (!jwtTokenProvider.validateToken(token)) {  // 改为这个
            throw new RuntimeException("Invalid token");
        }

        // Extract email from token
        String email = jwtTokenProvider.extractEmail(token);
        String role = jwtTokenProvider.extractRole(token);

        // Verify user still exists and is enabled
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Generate new token
        String newToken = jwtTokenProvider.generateToken(email, role);

        // Build response
        AuthDTO response = new AuthDTO();
        response.setToken(newToken);
        response.setRole(role);
        response.setEmail(email);
        response.setUserId(user.getId());
        response.setBusinessPersonId(
                user.getBusinessPerson() != null ? user.getBusinessPerson().getId() : null
        );

        return response;
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);  // 改为这个
    }
}