package app.auth;

import app.auth.dto.AuthDTO;
import app.auth.dto.AuthRequest;
import app.auth.InvalidCredentialsException;
import app.auth.InvalidTokenException;
import app.user.UserDisabledException;
import app.user.UserNotFoundException;
import app.user.User;
import app.user.UserRepository;
import app.auth.JwtTokenProvider;
import app.auth.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(InvalidCredentialsException::new);

        if (!user.getEnabled()) {
            throw new UserDisabledException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return buildAuthDTO(user, token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException();
        }

        String email = jwtTokenProvider.extractEmail(token);
        String role = jwtTokenProvider.extractRole(token);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));

        if (!user.getEnabled()) {
            throw new UserDisabledException();
        }

        String newToken = jwtTokenProvider.generateToken(email, role);

        return buildAuthDTO(user, newToken);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    private AuthDTO buildAuthDTO(User user, String token) {
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
}